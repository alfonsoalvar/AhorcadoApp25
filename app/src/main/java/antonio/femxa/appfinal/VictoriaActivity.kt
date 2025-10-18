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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.sp
import antonio.femxa.appfinal.ui.theme.AhorcadoApp25Theme
import kotlinx.coroutines.flow.collectLatest

class VictoriaActivity : ComponentActivity() {

    private val viewModel: EndGameViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val palabra = intent.getStringExtra("palabra_clave") ?: ""
        val musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", true)

        viewModel.initialize(palabra, musicaOnOff)

        mediaPlayer = MediaPlayer.create(this, R.raw.sonido_ganador).apply {
            isLooping = false
            setVolume(100f, 100f)
        }

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.musicaOn) {
                if (uiState.musicaOn) {
                    mediaPlayer?.start()
                }
            }

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collectLatest { event ->
                    when (event) {
                        is NavigationEvent.NavigateToCategoria -> {
                            val intent = Intent(this@VictoriaActivity, CategoriaActivity::class.java)
                            intent.putExtra("SonidoOn-Off", event.musicaOn)
                            startActivity(intent)
                            finish()
                        }
                        is NavigationEvent.NavigateToMenu -> {
                            val intent = Intent(this@VictoriaActivity, InicialActivity::class.java).apply {
                                putExtra("SonidoOn-Off", event.musicaOn)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                            finish()
                        }
                        is NavigationEvent.Share -> {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, event.message)
                            }
                            startActivity(Intent.createChooser(shareIntent, "Compartir victoria"))
                        }
                        else -> Log.d("VictoriaActivity", "Unhandled navigation event: $event")
                    }
                }
            }

            AhorcadoApp25Theme {
                VictoriaScreen(
                    palabra = uiState.palabra,
                    onJugarDeNuevoClicked = viewModel::onPlayAgainClicked,
                    onMenuPrincipalClicked = viewModel::onMainMenuClicked,
                    onCompartirClicked = viewModel::onShareClicked
                )
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onPlayAgainClicked()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun VictoriaScreen(
    palabra: String,
    onJugarDeNuevoClicked: () -> Unit,
    onMenuPrincipalClicked: () -> Unit,
    onCompartirClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "¡HAS GANADO!", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "La palabra era:", fontSize = 20.sp)
            Text(text = palabra, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onJugarDeNuevoClicked) {
                Text("Jugar de nuevo")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onMenuPrincipalClicked) {
                Text("Menú Principal")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onCompartirClicked) {
                Text("Compartir")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VictoriaScreenPreview() {
    AhorcadoApp25Theme {
        VictoriaScreen("PALABRA", {}, {}, {})
    }
}
