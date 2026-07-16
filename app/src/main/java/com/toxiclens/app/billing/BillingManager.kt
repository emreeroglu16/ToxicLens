package com.toxiclens.app.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

class BillingManager(
    context: Context,
    private val onPremiumChanged: (Boolean) -> Unit,
    private val onError: (String) -> Unit
) {
    companion object {
        const val PREMIUM_PRODUCT_ID = "read_between_premium"
    }

    private lateinit var billingClient: BillingClient
    private var premiumProductDetails: ProductDetails? = null

    init {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (
                    billingResult.responseCode ==
                    BillingClient.BillingResponseCode.OK
                ) {
                    purchases?.forEach { purchase ->
                        processPurchase(purchase)
                    }
                } else if (
                    billingResult.responseCode !=
                    BillingClient.BillingResponseCode.USER_CANCELED
                ) {
                    onError(billingResult.debugMessage)
                }
            }
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .enableAutoServiceReconnection()
            .build()
    }

    fun startConnection() {
        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(
                    billingResult: BillingResult
                ) {
                    if (
                        billingResult.responseCode ==
                        BillingClient.BillingResponseCode.OK
                    ) {
                        loadProductDetails()
                        checkExistingPurchases()
                    } else {
                        onError(billingResult.debugMessage)
                    }
                }

                override fun onBillingServiceDisconnected() = Unit
            }
        )
    }

    private fun processPurchase(purchase: Purchase) {
        val isPremiumPurchase =
            purchase.products.contains(PREMIUM_PRODUCT_ID) &&
                    purchase.purchaseState ==
                    Purchase.PurchaseState.PURCHASED

        if (!isPremiumPurchase) return

        onPremiumChanged(true)

        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(params) { billingResult ->
                if (
                    billingResult.responseCode !=
                    BillingClient.BillingResponseCode.OK
                ) {
                    onError(billingResult.debugMessage)
                }
            }
        }
    }

    private fun loadProductDetails() {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PREMIUM_PRODUCT_ID)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        billingClient.queryProductDetailsAsync(params) {
                billingResult,
                productDetailsResult ->

            if (
                billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
            ) {
                premiumProductDetails =
                    productDetailsResult.productDetailsList.firstOrNull()
            } else {
                onError(billingResult.debugMessage)
            }
        }
    }

    fun checkExistingPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) {
                billingResult,
                purchases ->

            if (
                billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
            ) {
                val premiumPurchase = purchases.firstOrNull { purchase ->
                    purchase.products.contains(PREMIUM_PRODUCT_ID) &&
                            purchase.purchaseState ==
                            Purchase.PurchaseState.PURCHASED
                }

                if (premiumPurchase != null) {
                    processPurchase(premiumPurchase)
                } else {
                    onPremiumChanged(false)
                }
            } else {
                onError(billingResult.debugMessage)
            }
        }
    }

    fun launchPurchase(
        activity: Activity,
        basePlanId: String
    ) {
        val productDetails = premiumProductDetails

        if (productDetails == null) {
            onError("Premium product is not available yet.")
            return
        }

        val offerToken = productDetails
            .subscriptionOfferDetails
            ?.firstOrNull { offer ->
                offer.basePlanId == basePlanId
            }
            ?.offerToken

        if (offerToken == null) {
            onError("Selected subscription plan is not available.")
            return
        }

        val productParams =
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(productParams)
            )
            .build()

        val result = billingClient.launchBillingFlow(
            activity,
            billingFlowParams
        )

        if (
            result.responseCode !=
            BillingClient.BillingResponseCode.OK
        ) {
            onError(result.debugMessage)
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }
}