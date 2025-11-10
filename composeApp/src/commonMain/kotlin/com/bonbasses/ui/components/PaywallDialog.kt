package com.bonbasses.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.ic_lock
import com.bonbasses.platform.iap.IAPManager
import com.bonbasses.platform.iap.IAPProduct
import com.bonbasses.platform.iap.PurchaseResult
import com.bonbasses.ui.theme.RobotoSlabFontFamily
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun PaywallDialog(
    iapManager: IAPManager,
    onDismiss: () -> Unit
) {
    var products by remember { mutableStateOf<List<IAPProduct>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    

    LaunchedEffect(Unit) {
        products = iapManager.getProducts()
        isLoading = false
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1A1410))
                .border(1.dp, Color(0xFF3D2A1F), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Unlock Premium Features",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RobotoSlabFontFamily(),
                    color = Color(0xFFAD8E7D),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Enhance your writing experience",
                    fontSize = 14.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    color = Color(0xFF8D7E6D),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                products.forEach { product ->
                    ProductCard(
                        product = product,
                        isLoading = isLoading,
                        onPurchase = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                when (val result = iapManager.purchase(product.id)) {
                                    is PurchaseResult.Success -> {
                                        isLoading = false
                                        onDismiss()
                                    }
                                    is PurchaseResult.Error -> {
                                        isLoading = false
                                        errorMessage = result.message
                                    }
                                    is PurchaseResult.Cancelled -> {
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        fontSize = 12.sp,
                        color = Color(0xFFE57373),
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Restore",
                        fontSize = 14.sp,
                        fontFamily = RobotoSlabFontFamily(),
                        color = Color(0xFF8D7E6D),
                        modifier = Modifier
                            .clickable(enabled = !isLoading) {
                                scope.launch {
                                    isLoading = true
                                    iapManager.restorePurchases()
                                    isLoading = false
                                    onDismiss()
                                }
                            }
                            .padding(8.dp)
                    )
                    
                    Text(
                        text = "Close",
                        fontSize = 14.sp,
                        fontFamily = RobotoSlabFontFamily(),
                        color = Color(0xFF8D7E6D),
                        modifier = Modifier
                            .clickable(enabled = !isLoading) { onDismiss() }
                            .padding(8.dp)
                    )
                }
            }
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFAD8E7D))
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: IAPProduct,
    isLoading: Boolean,
    onPurchase: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (product.isPurchased) Color(0xFF2D4A2F)
                else Color(0xFF2A1F17)
            )
            .border(
                1.dp,
                if (product.isPurchased) Color(0xFF4CAF50)
                else Color(0xFF3D2A1F),
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isLoading && !product.isPurchased) { onPurchase() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!product.isPurchased) {
                Image(
                    painter = painterResource(Res.drawable.ic_lock),
                    contentDescription = "Locked",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RobotoSlabFontFamily(),
                    color = if (product.isPurchased) Color(0xFF81C784) else Color(0xFFAD8E7D)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    color = Color(0xFF8D7E6D)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            if (product.isPurchased) {
                Text(
                    text = "✓ Owned",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RobotoSlabFontFamily(),
                    color = Color(0xFF81C784)
                )
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF7E512C))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = product.price,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = RobotoSlabFontFamily(),
                        color = Color.White
                    )
                }
            }
        }
    }
}
