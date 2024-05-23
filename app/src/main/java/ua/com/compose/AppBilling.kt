package ua.com.compose

import android.app.Activity
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ua.com.compose.data.DataStoreKey
import ua.com.compose.extension.dataStore

class AppBilling(context: Context) {

    companion object {
        const val PREMIUM_PRODUCT_ID = "color_picker_premium"
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                purchases.forEach { handlePurchase(it) }
            }
        }

    private val purchasesResponseListener =
        PurchasesResponseListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                GlobalScope.launch {
                    dataStore.edit { preferences ->
                        preferences[DataStoreKey.KEY_PREMIUM] = purchases.filter { it.products.any { it == PREMIUM_PRODUCT_ID } }.any { it.purchaseState == Purchase.PurchaseState.PURCHASED && it.isAcknowledged }
                    }
                }
                purchases.forEach { handlePurchase(it) }
            }
        }

    private val acknowledgePurchaseResponseListener =
        AcknowledgePurchaseResponseListener { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                GlobalScope.launch {
                    dataStore.edit { preferences ->
                        preferences[DataStoreKey.KEY_PREMIUM] = true
                    }
                }
            }
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private val queryProductDetailsParams =
        QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PREMIUM_PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build())
            )
            .build()

    private val params = QueryPurchasesParams.newBuilder()
        .setProductType(BillingClient.ProductType.INAPP)

    init {
        startConnections()
    }

    fun startConnections() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                billingClient.queryPurchasesAsync(params.build(), purchasesResponseListener)
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build(), acknowledgePurchaseResponseListener)
            }
        }
    }


    fun buyPremium(activity: Activity) {
        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->
            productDetailsList.firstOrNull()?.let { product ->
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .build()
                )

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

                billingClient.launchBillingFlow(activity, billingFlowParams)
            }
        }
    }
}