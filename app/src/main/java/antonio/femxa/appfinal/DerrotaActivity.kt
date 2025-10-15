package antonio.femxa.appfinal

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DerrotaActivity : ComponentActivity() {

    private var palabra: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private var musicaOnOff: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        palabra = intent.getStringExtra("palabra_clave")
        musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", false)

        if (musicaOnOff) {
            mediaPlayer = MediaPlayer.create(this, R.raw.sonido_perdedor)
            mediaPlayer?.isLooping = false
            mediaPlayer?.setVolume(100f, 100f)
            mediaPlayer?.start()
        }

        setContent {
            DerrotaScreen(
                palabra = palabra ?: "",
                onJugarDeNuevo = { jugarDeNuevo() },
                onMenuPrincipal = { irAMenuPrincipal() }
            )
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                jugarDeNuevo()
                finish()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun jugarDeNuevo() {
        val intent = Intent(this, CategoriaActivity::class.java)
        intent.putExtra("SonidoOn-Off", musicaOnOff)
        startActivity(intent)
        finish()
    }

    private fun irAMenuPrincipal() {
        val intent = Intent(this, InicialActivity::class.java)
        intent.putExtra("SonidoOn-Off", musicaOnOff)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun DerrotaScreen(palabra: String, onJugarDeNuevo: () -> Unit, onMenuPrincipal: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¡HAS PERDIDO!", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.pantallagameover),
                contentDescription = "Derrota",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("La palabra era:", fontSize = 20.sp, textAlign = TextAlign.Center)
            Text(palabra, fontSize = 24.sp, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onJugarDeNuevo) {
                Text("JUGAR DE NUEVO")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onMenuPrincipal) {
                Text("MENÚ PRINCIPAL")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DerrotaScreenPreview() {
    DerrotaScreen(palabra = "ANDROID", onJugarDeNuevo = {}, onMenuPrincipal = {})
}
