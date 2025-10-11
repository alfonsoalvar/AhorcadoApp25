package antonio.femxa.appfinal

//import android.R
import android.content.Intent
import android.media.MediaPlayer
import antonio.femxa.appfinal.util.Constantes
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdRequest.*
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InicialActivity : AppCompatActivity() {
    var mediaPlayer: MediaPlayer? = null
    var musicaOnOff: Boolean = false

    val IDBLOQUEANUNCIO_INTER = "ca-app-pub-9910445535228761/8258514024"
    val IDBLOQUEANUNCIO_INTER_RECOMPENSADO = "ca-app-pub-9910445535228761/2022540677"
    var interstitialAd : InterstitialAd? = null

    var interstitialAdRecompensado : RewardedInterstitialAd? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicial)

        //iniciarAnuncios()
        iniciarAnunciosRecompensado()

        mediaPlayer = MediaPlayer.create(this, R.raw.inicio1)
        mediaPlayer!!.isLooping = true
        mediaPlayer!!.setVolume(100f, 100f)

        //ponerTexto()

       musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", false)

        val ib = findViewById<Button>(R.id.botonsonido)


        if (musicaOnOff) {
            mediaPlayer!!.start()
        }


        ib.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                ib.text = "SONIDO ON"
                musicaOnOff = false
            } else {
                ib.text = "SONIDO OFF"
                mediaPlayer!!.start()
                musicaOnOff = true
            }
        }

        //botón hacia atrás

        //acción botón hacia atrás
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    fun aJugar(v: View?) {
        val intent = Intent(
            this,
            CategoriaActivity::class.java
        )

        if (musicaOnOff) {
            intent.putExtra("SonidoOn-Off", true)
        } else {
            intent.putExtra("SonidoOn-Off", false)
        }

        startActivity(intent)
    }



    fun abrirCreditos(v: View?) {
        val intent = Intent(this, CreditosActivity::class.java)
        startActivity(intent)
    }



    fun iniciarAnuncios ()
    {
        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@InicialActivity) {
                initializationStatus : InitializationStatus ->

                Log.d(Constantes.ETIQUETA_LOG, "Inicialización de anuncios completada")
                InterstitialAd.load(
                    this@InicialActivity,
                    IDBLOQUEANUNCIO_INTER,
                    Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            Log.d(Constantes.ETIQUETA_LOG, "Anuncio Cargado.")
                            interstitialAd = ad
                            interstitialAd?.show(this@InicialActivity)
                            interstitialAd?.fullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        Log.d(Constantes.ETIQUETA_LOG, "Ad was dismissed.")
                                        // Don't forget to set the ad reference to null so you
                                        // don't show the ad a second time.
                                        interstitialAd = null
                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                        // Called when fullscreen content failed to show.
                                        Log.d(Constantes.ETIQUETA_LOG, "Ad failed to show.")
                                        // Don't forget to set the ad reference to null so you
                                        // don't show the ad a second time.
                                        interstitialAd = null
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d(Constantes.ETIQUETA_LOG, "Ad showed fullscreen content.")
                                    }

                                    override fun onAdImpression() {
                                        // Called when an impression is recorded for an ad.
                                        Log.d(Constantes.ETIQUETA_LOG, "Ad recorded an impression.")
                                    }

                                    override fun onAdClicked() {
                                        // Called when ad is clicked.
                                        Log.d(Constantes.ETIQUETA_LOG, "Ad was clicked.")
                                    }
                                }
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.e (Constantes.ETIQUETA_LOG, adError.message)
                            interstitialAd = null
                        }
                    },
                )

            }
        }
    }


    fun iniciarAnunciosRecompensado ()
    {
        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@InicialActivity) {
                    initializationStatus : InitializationStatus ->

                Log.d(Constantes.ETIQUETA_LOG, "Inicialización de anuncios completada")
                RewardedInterstitialAd.load(
                    this@InicialActivity,
                    IDBLOQUEANUNCIO_INTER_RECOMPENSADO,
                    Builder().build(),
                    object : RewardedInterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedInterstitialAd) {
                            Log.d(Constantes.ETIQUETA_LOG, "Anuncio Cargado.")
                            interstitialAdRecompensado = ad
                            interstitialAdRecompensado?.show(this@InicialActivity) { rewardItem ->
                                Log.d(Constantes.ETIQUETA_LOG, "User earned the reward. ${rewardItem.amount} ${rewardItem.type}")

                            }
                            interstitialAdRecompensado?.fullScreenContentCallback =
                                    object : FullScreenContentCallback() {
                                        override fun onAdDismissedFullScreenContent() {
                                            // Called when fullscreen content is dismissed.
                                            Log.d(Constantes.ETIQUETA_LOG, "Ad was dismissed.")
                                            // Don't forget to set the ad reference to null so you
                                            // don't show the ad a second time.
                                            interstitialAdRecompensado = null
                                        }

                                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                            // Called when fullscreen content failed to show.
                                            Log.d(Constantes.ETIQUETA_LOG, "Ad failed to show.")
                                            // Don't forget to set the ad reference to null so you
                                            // don't show the ad a second time.
                                            interstitialAdRecompensado = null
                                        }

                                        override fun onAdShowedFullScreenContent() {
                                            // Called when fullscreen content is shown.
                                            Log.d(Constantes.ETIQUETA_LOG, "Ad showed fullscreen content.")
                                        }

                                        override fun onAdImpression() {
                                            // Called when an impression is recorded for an ad.
                                            Log.d(Constantes.ETIQUETA_LOG, "Ad recorded an impression.")
                                        }

                                        override fun onAdClicked() {
                                            // Called when ad is clicked.
                                            Log.d(Constantes.ETIQUETA_LOG, "Ad was clicked.")
                                        }
                                    }
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.e (Constantes.ETIQUETA_LOG, adError.message)
                            interstitialAdRecompensado = null
                        }
                    },
                )

            }
        }
    }
}
