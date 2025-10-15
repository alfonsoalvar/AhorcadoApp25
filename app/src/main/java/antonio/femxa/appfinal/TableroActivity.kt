package antonio.femxa.appfinal

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import antonio.femxa.appfinal.ui.theme.AhorcadoApp25Theme
import antonio.femxa.appfinal.util.Constantes.ETIQUETA_LOG

class TableroActivity : ComponentActivity() {

    private lateinit var palabra: String
    private var sonidoOnOff: Boolean = false
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        palabra = intent.getStringExtra("palabra_clave")?.uppercase() ?: ""
        sonidoOnOff = intent.getBooleanExtra("SonidoOn-Off", false)

        mediaPlayer = MediaPlayer.create(this, R.raw.durantejugar).apply {
            isLooping = true
            setVolume(100f, 100f)
            if (sonidoOnOff) {
                start()
            }
        }

        setContent {
            AhorcadoApp25Theme {
                TableroScreen(
                    palabra = palabra,
                    categoria = intent.getStringExtra("categoria_seleccionada") ?: "",
                    sonidoOn = sonidoOnOff,
                    onSonidoToggle = {
                        sonidoOnOff = !sonidoOnOff
                        if (mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.pause()
                        } else {
                            mediaPlayer?.start()
                        }
                    },
                    onJuegoTerminado = { victoria ->
                        if (victoria) {
                            navegarAVictoria()
                        } else {
                            navegarADerrota()
                        }
                    }
                )
            }
        }
    }

    private fun navegarAVictoria() {
        val intent = Intent(this, VictoriaActivity::class.java).apply {
            putExtra("palabra_clave", palabra)
            putExtra("SonidoOn-Off", sonidoOnOff)
        }
        startActivity(intent)
    }

    private fun navegarADerrota() {
        val intent = Intent(this, DerrotaActivity::class.java).apply {
            putExtra("palabra_clave", palabra)
            putExtra("SonidoOn-Off", sonidoOnOff)
        }
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.stop()
    }
}

@Composable
fun TableroScreen(
    palabra: String,
    categoria: String,
    sonidoOn: Boolean,
    onSonidoToggle: () -> Unit,
    onJuegoTerminado: (Boolean) -> Unit
) {
    var letrasPulsadas by remember { mutableStateOf(setOf<Char>()) }
    var errores by remember { mutableStateOf(0) }

    val palabraSinEspacios = palabra.replace(" ", "")
    val letrasAcertadas = palabraSinEspacios.count { it in letrasPulsadas }
    val victoria = letrasAcertadas == palabraSinEspacios.length
    val derrota = errores >= 6

    LaunchedEffect(victoria, derrota) {
        if (victoria || derrota) {
            onJuegoTerminado(victoria)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = categoria, fontSize = 20.sp)
                IconButton(onClick = onSonidoToggle) {
                    Icon(
                        painter = painterResource(id = if (sonidoOn) R.drawable.ic_volume_up else R.drawable.ic_volume_off),
                        contentDescription = "Sonido"
                    )
                }
            }

            AhorcadoImage(errores)

            PalabraOculta(palabra, letrasPulsadas)

            Teclado(letrasPulsadas) { letra ->
                if (!victoria && !derrota) {
                    letrasPulsadas = letrasPulsadas + letra
                    if (letra !in palabra) {
                        errores++
                    }
                    /*Log.d(ETIQUETA_LOG, "Letra pulsada: $letra")
                    Log.d(ETIQUETA_LOG, letrasAcertadas.toString())
                    Log.d(ETIQUETA_LOG, palabraSinEspacios)
                    Log.d(ETIQUETA_LOG, palabraSinEspacios.length.toString())*/
                }
            }
        }
    }
}

@Composable
fun AhorcadoImage(errores: Int) {
    val imagenes = listOf(
        R.drawable.ic_cuerda,
        R.drawable.ic_cabeza,
        R.drawable.ic_cuerpo,
        R.drawable.ic_brazo,
        R.drawable.ic_brazos,
        R.drawable.ic_pierna
    )

    Icon(
        painter = painterResource(id = if(errores < imagenes.size) imagenes[errores] else imagenes.last()),
        contentDescription = "Ahorcado",
        modifier = Modifier.size(150.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PalabraOculta(palabra: String, letrasPulsadas: Set<Char>) {
    FlowRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center
    ) {
        palabra.forEach { char ->
            if (char == ' ') {
                Spacer(modifier = Modifier.size(16.dp))
            } else {
                Text(
                    text = if (letrasPulsadas.contains(char)) char.toString() else "_",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Teclado(letrasPulsadas: Set<Char>, onLetraPulsada: (Char) -> Unit) {
    val teclado = "QWERTYUIOPASDFGHJKLZXCVBNM"

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        teclado.forEach { letra ->
            //Log.d(ETIQUETA_LOG, "Letra: $letra")
            val isEnabled = letra !in letrasPulsadas
            val backgroundColor = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            val textColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

            Surface(
                modifier = Modifier
                    .padding(2.dp)
                    .size(48.dp)
                    .clickable(enabled = isEnabled) { onLetraPulsada(letra) },
                shape = MaterialTheme.shapes.medium,
                color = backgroundColor,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = letra.toString(),
                        color = textColor,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TableroPreview() {
    TableroScreen("airbag", "peliculas", false, {}, {})
}
