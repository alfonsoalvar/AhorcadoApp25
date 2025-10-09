package antonio.femxa.appfinal

//import android.R
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InicialActivity : AppCompatActivity() {
     var mediaPlayer: MediaPlayer? = null
     var musicaOnOff: Boolean = false

     val IDBLOUEANUNCIO_INTER = "ca-app-pub-9910445535228761/8258514024"
     var interstitialAd : InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicial)

        iniciarAnuncios()

        mediaPlayer = MediaPlayer.create(this, R.raw.inicio1)
        mediaPlayer!!.isLooping = true
        mediaPlayer!!.setVolume(100f, 100f)

        //ponerTexto()

       musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", true)

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

                Log.d("MIAPP", "Inicialización de anuncios completada")
                InterstitialAd.load(
                    this@InicialActivity,
                    IDBLOUEANUNCIO_INTER,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            Log.d("MIAPP", "Anuncio Cargado.")
                            interstitialAd = ad
                            interstitialAd?.show(this@InicialActivity)
                            interstitialAd?.fullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        Log.d("MIAPP", "Ad was dismissed.")
                                        // Don't forget to set the ad reference to null so you
                                        // don't show the ad a second time.
                                        interstitialAd = null
                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                        // Called when fullscreen content failed to show.
                                        Log.d("MIAPP", "Ad failed to show.")
                                        // Don't forget to set the ad reference to null so you
                                        // don't show the ad a second time.
                                        interstitialAd = null
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("MIAPP", "Ad showed fullscreen content.")
                                    }

                                    override fun onAdImpression() {
                                        // Called when an impression is recorded for an ad.
                                        Log.d("MIAPP", "Ad recorded an impression.")
                                    }

                                    override fun onAdClicked() {
                                        // Called when ad is clicked.
                                        Log.d("MIAPP", "Ad was clicked.")
                                    }
                                }
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.e ("MIAPP", adError.message)
                            interstitialAd = null
                        }
                    },
                )

            }
        }
    }
}