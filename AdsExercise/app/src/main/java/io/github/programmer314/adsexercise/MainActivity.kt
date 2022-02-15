package io.github.programmer314.adsexercise

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : AppCompatActivity() {
    private var mRewardedAd: RewardedAd? = null
    private val TAG = "MainActivity"

    private var totalReward: Int = 0
    private lateinit var rewardTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()

        val adView = findViewById<AdView>(R.id.adView)
        adView.loadAd(adRequest)

        rewardTextView = findViewById(R.id.rewardTextView)
        updateReward(0)

        findViewById<Button>(R.id.rewardButton).tag = adRequest
    }

    fun startReward(view: View) {
        if (view.tag is AdRequest)
            setupRewardAd(view.tag as AdRequest)

    }

    private fun setupRewardAd(adRequest: AdRequest) {
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                mRewardedAd = rewardedAd
                showRewardedAd()
            }
        })
    }

    private fun showRewardedAd() {
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad was shown.")
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
                mRewardedAd = null
            }
        }

        mRewardedAd?.show(this) { rewardItem ->
                updateReward(rewardItem.amount)
                Log.d(TAG, "User earned the reward.")
        }
            ?: Log.d(TAG, "The rewarded ad wasn't ready yet.")
    }

    private fun updateReward(amount: Int) {
        totalReward += amount
        rewardTextView.text = "Reward: $totalReward"
    }
}