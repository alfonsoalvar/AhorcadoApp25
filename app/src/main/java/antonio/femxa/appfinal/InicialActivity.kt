package antonio.femxa.appfinal

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import antonio.femxa.appfinal.ui.theme.AhorcadoApp25Theme
import kotlinx.coroutines.flow.collectLatest

class InicialActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val viewModel: InicialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", false)
        viewModel.setInitialMusicState(musicaOnOff)

        mediaPlayer = MediaPlayer.create(this, R.raw.inicio1).apply {
            isLooping = true
            setVolume(100f, 100f)
        }

        val versionName = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.PackageInfoFlags.of(0)).versionName
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0).versionName
        }
        Log.d("InicialActivity", "Version Name: $versionName")

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.musicaOn) {
                if (uiState.musicaOn && mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                } else if (!uiState.musicaOn && mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                }
            }

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collectLatest { event ->
                    when (event) {
                        is NavigationEvent.NavigateToCategoria -> aJugar(event.musicaOn)
                        is NavigationEvent.NavigateToCreditos -> abrirCreditos()
                        else -> Log.d("InicialActivity", "Unhandled navigation event: $event")
                    }
                }
            }

            AhorcadoApp25Theme {
                InicialScreen(
                    musicaOn = uiState.musicaOn,
                    onJugarClicked = viewModel::onJugarClicked,
                    onCreditosClicked = viewModel::onCreditosClicked,
                    onSonidoClicked = viewModel::onSonidoClicked,
                    versionName = versionName
                )
            }
        }

        setupBackButton()
    }

    private fun aJugar(musicaOn: Boolean) {
        val intent = Intent(this, CategoriaActivity::class.java).apply {
            putExtra("SonidoOn-Off", musicaOn)
        }
        startActivity(intent)
    }

    private fun abrirCreditos() {
        val intent = Intent(this, CreditosActivity::class.java)
        startActivity(intent)
    }

    private fun setupBackButton() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        // La música se reanuda a través del LaunchedEffect si es necesario
        if (viewModel.uiState.value.musicaOn) {
            mediaPlayer?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun InicialScreen(
    musicaOn: Boolean,
    onJugarClicked: () -> Unit,
    onCreditosClicked: () -> Unit,
    onSonidoClicked: () -> Unit,
    versionName: String?
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
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
                Button(onClick = onSonidoClicked) {
                    Text(if (musicaOn) "SONIDO OFF" else "SONIDO ON")
                }
            }

            // Texto de la versión posicionado abajo a la derecha
            Text(
                text = "v$versionName",
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Alineación a la esquina
                    .padding(16.dp), // Margen respecto a los bordes
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f) // Color sutil
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AhorcadoApp25Theme {
        InicialScreen(true, {}, {}, {}, "3.1")
    }
}
