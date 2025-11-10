package com.bonbasses.platform.iap

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class IAPManager {
    companion object {
        @Volatile
        private var instance: IAPManager? = null
        private var appContext: Context? = null
        
        fun getInstance(context: Context? = null): IAPManager {
            if (context != null && appContext == null) {
                appContext = context.applicationContext
            }
            return instance ?: synchronized(this) {
                instance ?: IAPManager().also { 
                    instance = it
                    it.initialize(appContext)
                }
            }
        }
        
        operator fun invoke(): IAPManager = getInstance()
    }
    
    private val _purchaseState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    actual val purchaseState: StateFlow<Map<String, Boolean>> = _purchaseState.asStateFlow()
    
    private lateinit var billingClient: BillingClient
    private var currentActivity: Activity? = null
    private val purchasesUpdatedListeners = mutableListOf<(BillingResult, List<Purchase>?) -> Unit>()
    
    actual fun initialize(context: Any?) {
        if (context is Context && appContext == null) {
            appContext = context.applicationContext
        }
        
        if (appContext == null) return

        billingClient = BillingClient.newBuilder(appContext!!)
            .setListener { billingResult, purchases ->
                handlePurchaseUpdates(billingResult, purchases)
            }
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchases()
                }
            }
            
            override fun onBillingServiceDisconnected() {
            }
        })
    }
    
    private fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchaseUpdates(billingResult, purchases)
            }
        }
    }
    
    private fun handlePurchaseUpdates(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            val purchaseMap = mutableMapOf<String, Boolean>()
            
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    if (!purchase.isAcknowledged) {
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()
                        
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { _ ->
                        }
                    }

                    purchase.products.forEach { productId ->
                        purchaseMap[productId] = true
                    }
                }
            }

            val fullMap = mutableMapOf<String, Boolean>()
            fullMap[IAPProducts.TIMER_10_MIN] = purchaseMap[IAPProducts.TIMER_10_MIN] ?: false
            fullMap[IAPProducts.CANVAS_PACK] = purchaseMap[IAPProducts.CANVAS_PACK] ?: false
            
            _purchaseState.value = fullMap
        }

        purchasesUpdatedListeners.forEach { it(billingResult, purchases) }
        purchasesUpdatedListeners.clear()
    }
    
    actual suspend fun getProducts(): List<IAPProduct> {
        if (!::billingClient.isInitialized || !billingClient.isReady) {
            return emptyList()
        }
        
        return suspendCancellableCoroutine { continuation ->
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(IAPProducts.TIMER_10_MIN)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(IAPProducts.CANVAS_PACK)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()
            
            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val products = productDetailsList.map { details ->
                        IAPProduct(
                            id = details.productId,
                            title = details.name,
                            description = details.description,
                            price = details.oneTimePurchaseOfferDetails?.formattedPrice ?: "$1.99",
                            isPurchased = isPurchased(details.productId)
                        )
                    }
                    continuation.resume(products)
                } else {
                    continuation.resume(emptyList())
                }
            }
        }
    }
    
    actual fun isPurchased(productId: String): Boolean {
        return _purchaseState.value[productId] == true
    }
    
    fun setActivity(activity: Activity) {
        currentActivity = activity
    }
    
    actual suspend fun purchase(productId: String): PurchaseResult {
        if (!::billingClient.isInitialized || !billingClient.isReady) {
            return PurchaseResult.Error("Billing client not ready")
        }
        
        if (currentActivity == null) {
            return PurchaseResult.Error("Activity not set")
        }
        
        return suspendCancellableCoroutine { continuation ->
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()
            
            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && 
                    productDetailsList.isNotEmpty()) {
                    
                    val productDetails = productDetailsList[0]

                    val flowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                            listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .build()
                            )
                        )
                        .build()

                    purchasesUpdatedListeners.add { result, purchases ->
                        when (result.responseCode) {
                            BillingClient.BillingResponseCode.OK -> {
                                if (purchases != null && purchases.any { it.products.contains(productId) }) {
                                    continuation.resume(PurchaseResult.Success)
                                }
                            }
                            BillingClient.BillingResponseCode.USER_CANCELED -> {
                                continuation.resume(PurchaseResult.Cancelled)
                            }
                            else -> {
                                continuation.resume(PurchaseResult.Error(result.debugMessage))
                            }
                        }
                    }
                    
                    val launchResult = billingClient.launchBillingFlow(currentActivity!!, flowParams)
                    
                    if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        purchasesUpdatedListeners.clear()
                        continuation.resume(PurchaseResult.Error(launchResult.debugMessage))
                    }
                } else {
                    continuation.resume(PurchaseResult.Error("Product not found"))
                }
            }
        }
    }
    
    actual suspend fun restorePurchases(): PurchaseResult {
        if (!::billingClient.isInitialized || !billingClient.isReady) {
            return PurchaseResult.Error("Billing client not ready")
        }
        
        return suspendCancellableCoroutine { continuation ->
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            
            billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    handlePurchaseUpdates(billingResult, purchases)
                    continuation.resume(PurchaseResult.Success)
                } else {
                    continuation.resume(PurchaseResult.Error(billingResult.debugMessage))
                }
            }
        }
    }
}

actual fun createIAPManager(): IAPManager {
    return IAPManager.getInstance()
}
