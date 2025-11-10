package com.bonbasses.platform.iap

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.StoreKit.*
import platform.Foundation.*
import platform.darwin.NSObject
import kotlinx.cinterop.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
actual class IAPManager : NSObject(), SKProductsRequestDelegateProtocol, SKPaymentTransactionObserverProtocol {
    private val _purchaseState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    actual val purchaseState: StateFlow<Map<String, Boolean>> = _purchaseState

    private var productRequest: SKProductsRequest? = null
    private var productsCallback: ((List<*>) -> Unit)? = null
    private var purchaseCallback: ((Boolean, String?) -> Unit)? = null
    
    init {
        SKPaymentQueue.defaultQueue().addTransactionObserver(this)
        loadPurchasedProducts()
    }
    
    actual fun initialize(context: Any?) {
    }

    actual suspend fun getProducts(): List<IAPProduct> = withContext(Dispatchers.Main) {
        suspendCoroutine { continuation ->
            val productIds = setOf(
                IAPProducts.TIMER_10_MIN,
                IAPProducts.CANVAS_PACK
            )
            
            val request = SKProductsRequest(productIdentifiers = productIds as Set<Any>)
            request.delegate = this@IAPManager
            
            productsCallback = { products ->
                @Suppress("UNCHECKED_CAST")
                val skProducts = products as List<SKProduct>
                val iapProducts = skProducts.map { product ->
                    val priceFormatter = NSNumberFormatter()
                    priceFormatter.formatterBehavior = NSNumberFormatterBehavior10_4
                    priceFormatter.numberStyle = NSNumberFormatterCurrencyStyle
                    priceFormatter.locale = product.priceLocale
                    val priceString = priceFormatter.stringFromNumber(product.price) ?: ""
                    
                    IAPProduct(
                        id = product.productIdentifier,
                        title = product.localizedTitle,
                        description = product.localizedDescription,
                        price = priceString,
                        isPurchased = isPurchased(product.productIdentifier)
                    )
                }
                continuation.resume(iapProducts)
            }
            
            request.start()
            productRequest = request
        }
    }

    actual suspend fun purchase(productId: String): PurchaseResult = withContext(Dispatchers.Main) {
        suspendCoroutine { continuation ->
            if (!SKPaymentQueue.canMakePayments()) {
                continuation.resume(PurchaseResult.Error("In-app purchases are disabled on this device"))
                return@suspendCoroutine
            }

            val productIds = setOf(productId)
            val request = SKProductsRequest(productIdentifiers = productIds as Set<Any>)
            request.delegate = object : NSObject(), SKProductsRequestDelegateProtocol {
                override fun productsRequest(request: SKProductsRequest, didReceiveResponse: SKProductsResponse) {
                    @Suppress("UNCHECKED_CAST")
                    val products = didReceiveResponse.products as List<SKProduct>
                    if (products.isNotEmpty()) {
                        val payment = SKPayment.paymentWithProduct(products.first())
                        SKPaymentQueue.defaultQueue().addPayment(payment)

                        purchaseCallback = { success, error ->
                            when {
                                success -> {
                                    savePurchasedProduct(productId)
                                    continuation.resume(PurchaseResult.Success)
                                }
                                error == "cancelled" -> continuation.resume(PurchaseResult.Cancelled)
                                else -> continuation.resume(PurchaseResult.Error(error ?: "Purchase failed"))
                            }
                        }
                    } else {
                        continuation.resume(PurchaseResult.Error("Product not found"))
                    }
                }

                override fun request(request: SKRequest, didFailWithError: NSError) {
                    continuation.resume(PurchaseResult.Error(didFailWithError.localizedDescription))
                }
            }
            request.start()
        }
    }

    actual suspend fun restorePurchases(): PurchaseResult = withContext(Dispatchers.Main) {
        suspendCoroutine { continuation ->
            purchaseCallback = { success, error ->
                if (success) {
                    continuation.resume(PurchaseResult.Success)
                } else {
                    continuation.resume(PurchaseResult.Error(error ?: "Restore failed"))
                }
            }
            SKPaymentQueue.defaultQueue().restoreCompletedTransactions()
        }
    }

    actual fun isPurchased(productId: String): Boolean {
        return _purchaseState.value[productId] ?: false
    }
    
    private fun savePurchasedProduct(productId: String) {
        val state = _purchaseState.value.toMutableMap()
        state[productId] = true
        _purchaseState.value = state
        
        NSUserDefaults.standardUserDefaults.setBool(true, forKey = "purchased_$productId")
        NSUserDefaults.standardUserDefaults.synchronize()
    }
    
    private fun loadPurchasedProducts() {
        val state = mutableMapOf<String, Boolean>()
        listOf(IAPProducts.TIMER_10_MIN, IAPProducts.CANVAS_PACK).forEach { productId ->
            val purchased = NSUserDefaults.standardUserDefaults.boolForKey("purchased_$productId")
            if (purchased) {
                state[productId] = true
            }
        }
        _purchaseState.value = state
    }
    
    override fun productsRequest(request: SKProductsRequest, didReceiveResponse: SKProductsResponse) {
        val products = didReceiveResponse.products
        productsCallback?.invoke(products)
        productsCallback = null
    }
    
    override fun request(request: SKRequest, didFailWithError: NSError) {
        productsCallback?.invoke(emptyList<SKProduct>())
        productsCallback = null
    }
    
    override fun paymentQueue(queue: SKPaymentQueue, updatedTransactions: List<*>) {
        @Suppress("UNCHECKED_CAST")
        val txList = updatedTransactions as List<SKPaymentTransaction>
        txList.forEach { trans ->
            when (trans.transactionState) {
                SKPaymentTransactionState.SKPaymentTransactionStatePurchased -> {
                    SKPaymentQueue.defaultQueue().finishTransaction(trans)
                    val productId = trans.payment.productIdentifier
                    savePurchasedProduct(productId)
                    purchaseCallback?.invoke(true, null)
                    purchaseCallback = null
                }
                SKPaymentTransactionState.SKPaymentTransactionStateFailed -> {
                    SKPaymentQueue.defaultQueue().finishTransaction(trans)
                    val error = trans.error?.localizedDescription
                    val isCancelled = trans.error?.code == 2L
                    purchaseCallback?.invoke(false, if (isCancelled) "cancelled" else error)
                    purchaseCallback = null
                }
                SKPaymentTransactionState.SKPaymentTransactionStateRestored -> {
                    SKPaymentQueue.defaultQueue().finishTransaction(trans)
                    val productId = trans.payment.productIdentifier
                    savePurchasedProduct(productId)
                }
                else -> {}
            }
        }
    }
    
    override fun paymentQueueRestoreCompletedTransactionsFinished(queue: SKPaymentQueue) {
        purchaseCallback?.invoke(true, null)
        purchaseCallback = null
    }
    
    override fun paymentQueue(queue: SKPaymentQueue, restoreCompletedTransactionsFailedWithError: NSError) {
        purchaseCallback?.invoke(false, restoreCompletedTransactionsFailedWithError.localizedDescription)
        purchaseCallback = null
    }
}

actual fun createIAPManager(): IAPManager = IAPManager()
