package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val context: Context) {

    companion object {
        const val ADMOB_APP_ID = "ca-app-pub-1784850147682581~1472952003"
        const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-1784850147682581/9327391296"
        const val REWARDED_AD_UNIT_ID = "ca-app-pub-1784850147682581/9918418235"
        private const val MIN_INTERSTITIAL_INTERVAL_MS = 60_000L // 60 seconds
        private const val TAG = "WordPro_AdManager"
    }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private var eligibleTriggerCount = 0
    private var lastInterstitialTime = 0L

    init {
        try {
            MobileAds.initialize(context) {
                Log.d(TAG, "MobileAds initialized")
                loadInterstitialAd()
                loadRewardedAd()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MobileAds", e)
        }
    }

    fun loadInterstitialAd() {
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d(TAG, "Interstitial ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        Log.w(TAG, "Interstitial ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading interstitial ad", e)
        }
    }

    fun loadRewardedAd() {
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        Log.d(TAG, "Rewarded ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading rewarded ad", e)
        }
    }

    /**
     * Trigger an eligible interstitial ad checkpoint.
     * Frequency cap: show every 2nd eligible trigger, minimum 60-second interval.
     */
    fun checkAndShowInterstitial(activity: Activity?, onComplete: () -> Unit = {}) {
        eligibleTriggerCount++
        val currentTime = System.currentTimeMillis()
        val timeSinceLast = currentTime - lastInterstitialTime

        val shouldShow = (eligibleTriggerCount % 2 == 0) && (timeSinceLast >= MIN_INTERSTITIAL_INTERVAL_MS)

        if (shouldShow && interstitialAd != null && activity != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastInterstitialTime = System.currentTimeMillis()
                    loadInterstitialAd()
                    onComplete()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadInterstitialAd()
                    onComplete()
                }
            }
            interstitialAd?.show(activity)
        } else {
            onComplete()
        }
    }

    /**
     * Show Rewarded Ad to grant extra hints.
     * If ad is ready, displays AdMob rewarded ad.
     * If ad is not ready (e.g. offline), calls fallback simulation so user reward is granted.
     */
    fun showRewardedAd(
        activity: Activity?,
        onRewardEarned: (rewardAmount: Int) -> Unit,
        onAdUnavailable: () -> Unit
    ) {
        val currentAd = rewardedAd
        if (currentAd != null && activity != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    loadRewardedAd()
                    onAdUnavailable()
                }
            }
            currentAd.show(activity) { rewardItem ->
                val amount = rewardItem.amount.takeIf { it > 0 } ?: 2
                onRewardEarned(amount)
            }
        } else {
            // Not ready or offline
            onAdUnavailable()
        }
    }
}
