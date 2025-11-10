package com.bonbasses.platform.iap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

object IAPProducts {
    const val TIMER_10_MIN = "com.bonbasses.timer10"
    const val CANVAS_PACK = "com.bonbasses.canvaspack"
}

data class IAPProduct(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val isPurchased: Boolean = false
)

sealed class PurchaseResult {
    object Success : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
    object Cancelled : PurchaseResult()
}

expect class IAPManager() {
    
    fun initialize(context: Any?)
    
    
    suspend fun getProducts(): List<IAPProduct>
    
    
    fun isPurchased(productId: String): Boolean
    
    
    suspend fun purchase(productId: String): PurchaseResult
    
    
    suspend fun restorePurchases(): PurchaseResult
    
    
    val purchaseState: StateFlow<Map<String, Boolean>>
}

expect fun createIAPManager(): IAPManager
