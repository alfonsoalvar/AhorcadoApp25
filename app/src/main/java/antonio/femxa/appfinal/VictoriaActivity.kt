package antonio.femxa.appfinal

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class VictoriaActivity : ComponentActivity() {
    private var palabra: String? = null
    private var musicaOnOff: Boolean = false
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        palabra = intent.getStringExtra("palabra_clave")
        musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", true)

        setContent {
            VictoriaScreen(
                palabra = palabra ?: "",
                onJugarDeNuevoClicked = {
                    val intent = Intent(this, CategoriaActivity::class.java)
                    intent.putExtra("SonidoOn-Off", musicaOnOff)
                    startActivity(intent)
                    finish()
                },
                onMenuPrincipalClicked = {
                    val intent = Intent(this, InicialActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                },
                onCompartirClicked = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "¡He ganado al Ahorcado! La palabra era: ${palabra ?: ""}")
                    }
                    startActivity(Intent.createChooser(shareIntent, "Compartir victoria"))
                }
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@VictoriaActivity, CategoriaActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer = MediaPlayer.create(this, R.raw.sonido_ganador)
        mediaPlayer?.isLooping = false
        mediaPlayer?.setVolume(100f, 100f)

        if (musicaOnOff) {
            mediaPlayer?.start()
        }
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
