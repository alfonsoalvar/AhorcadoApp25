package antonio.femxa.appfinal

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import antonio.femxa.appfinal.ui.theme.AhorcadoApp25Theme
import antonio.femxa.appfinal.util.Constantes
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InicialActivity : ComponentActivity() {
    var mediaPlayer: MediaPlayer? = null
    var musicaOnOff: Boolean = false

    val IDBLOQUEANUNCIO_INTER_RECOMPENSADO = "ca-app-pub-9910445535228761/2022540677"

    var interstitialAdRecompensado : RewardedInterstitialAd? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //iniciarAnunciosRecompensado()

        mediaPlayer = MediaPlayer.create(this, R.raw.inicio1)
        mediaPlayer!!.isLooping = true
        mediaPlayer!!.setVolume(100f, 100f)

        musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", false)


        if (musicaOnOff) {
            mediaPlayer!!.start()
        }

        setContent {
            AhorcadoApp25Theme {
                InicialScreen(
                    musicaOn = musicaOnOff,
                    onJugarClicked = { aJugar() },
                    onCreditosClicked = { abrirCreditos() },
                    onSonidoClicked = {
                        musicaOnOff = it
                        if (mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.pause()
                        } else {
                            mediaPlayer?.start()
                        }
                    }
                )
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

    fun aJugar() {
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



    fun abrirCreditos() {
        val intent = Intent(this, CreditosActivity::class.java)
        startActivity(intent)
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
                    AdRequest.Builder().build(),
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

@Composable
fun InicialScreen(
    musicaOn: Boolean,
    onJugarClicked: () -> Unit,
    onCreditosClicked: () -> Unit,
    onSonidoClicked: (Boolean) -> Unit
) {
    var musicaState by remember { mutableStateOf(musicaOn) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onJugarClicked) {
                Text("A JUGAR")
            }
            Button(onClick = onCreditosClicked) {
                Text("CRÉDITOS")
            }
            Button(onClick = {
                musicaState = !musicaState
                onSonidoClicked(musicaState)
            }) {
                Text(if (musicaState) "SONIDO OFF" else "SONIDO ON")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    InicialScreen(true, {}, {}, {})
}
