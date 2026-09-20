package com.blockpubs.app

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var protectionManager: ProtectionManager
    private var rewardedAd: RewardedAd? = null
    private var protectionTimer: CountDownTimer? = null

    private lateinit var timerText: TextView
    private lateinit var statusText: TextView
    private lateinit var btnActivate: Button
    private lateinit var adsPlaceholder: View

    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-4635526759947866/5124517862"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        protectionManager = ProtectionManager(this)

        timerText = findViewById(R.id.timerText)
        statusText = findViewById(R.id.statusText)
        btnActivate = findViewById(R.id.btnActivate)
        adsPlaceholder = findViewById(R.id.adsPlaceholder)

        MobileAds.initialize(this) {
            loadRewardedAd()
        }

        btnActivate.setOnClickListener {
            if (rewardedAd == null) {
                loadRewardedAd()
                Toast.makeText(this, "Chargement de la pub...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            rewardedAd?.show(this) {
                activateProtection()
            }
        }

        updateProtectionState()
    }

    override fun onResume() {
        super.onResume()
        if (protectionManager.isActive()) {
            startProtectionTimer()
        } else {
            updateProtectionState()
        }
    }

    private fun loadRewardedAd() {
        val request = AdRequest.Builder().build()

        RewardedAd.load(this, AD_UNIT_ID, request, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                Toast.makeText(this@MainActivity, "Pub prête", Toast.LENGTH_SHORT).show()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
                Toast.makeText(
                    this@MainActivity,
                    "Erreur chargement pub: ${adError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun activateProtection() {
        protectionManager.activateForThirtyMinutes()
        startProtectionTimer()
        updateProtectionState()
    }

    private fun startProtectionTimer() {
        protectionTimer?.cancel()

        val remaining = protectionManager.remainingTimeMillis()
        if (remaining <= 0L) {
            protectionManager.clear()
            updateProtectionState()
            return
        }

        protectionTimer = object : CountDownTimer(remaining, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateProtectionState(millisUntilFinished)
            }

            override fun onFinish() {
                protectionManager.clear()
                updateProtectionState()
                loadRewardedAd()
            }
        }.start()
    }

    private fun updateProtectionState(remainingOverride: Long? = null) {
        val remaining = remainingOverride ?: protectionManager.remainingTimeMillis()

        if (remaining > 0L) {
            val totalSeconds = remaining / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            timerText.text = String.format(
                Locale.FRANCE,
                "Protection active: %02d:%02d",
                minutes,
                seconds
            )
            statusText.text = "Bloquage des pubs dans l’application activé"
            btnActivate.isEnabled = false
            adsPlaceholder.visibility = View.GONE
        } else {
            timerText.text = "Protection inactive"
            statusText.text = "Aucune protection active"
            btnActivate.isEnabled = true
            adsPlaceholder.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        protectionTimer?.cancel()
        super.onDestroy()
    }
}
