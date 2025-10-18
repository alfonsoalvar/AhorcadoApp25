package antonio.femxa.appfinal

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import antonio.femxa.appfinal.ui.theme.AhorcadoApp25Theme
import kotlinx.coroutines.flow.collectLatest

class TableroActivity : ComponentActivity() {

    private val viewModel: TableroViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Leer datos del Intent
        val palabra = intent.getStringExtra("palabra_clave") ?: ""
        val categoria = intent.getStringExtra("categoria_seleccionada") ?: ""
        val sonidoOn = intent.getBooleanExtra("SonidoOn-Off", false)

        // 2. Pasarlos UNA SOLA VEZ al ViewModel
        viewModel.iniciarJuego(palabra, categoria, sonidoOn)

        mediaPlayer = MediaPlayer.create(this, R.raw.durantejugar).apply {
            isLooping = true
            setVolume(100f, 100f)
        }

        setContent {
            // 3. La UI se suscribe al estado del ViewModel
            val uiState by viewModel.uiState.collectAsState()

            // Efecto para controlar la música basado en el estado del ViewModel
            LaunchedEffect(uiState.sonidoOn) {
                if (uiState.sonidoOn) {
                    mediaPlayer?.start()
                } else {
                    mediaPlayer?.pause()
                }
            }

            // Efecto para observar la navegación
            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collectLatest { event ->
                    when (event) {
                        is NavigationEvent.NavigateToVictoria -> navegarAVictoria(event.palabra, event.musicaOn)
                        is NavigationEvent.NavigateToDerrota -> navegarADerrota(event.palabra, event.musicaOn)
                        else -> Log.d("TableroActivity", "Unhandled navigation event: $event")
                    }
                }
            }

            AhorcadoApp25Theme {
                // 4. La pantalla solo recibe el estado y notifica eventos
                TableroScreen(
                    uiState = uiState,
                    onSonidoToggle = viewModel::onSonidoToggle,
                    onLetraPulsada = viewModel::onLetraPulsada
                )
            }
        }
    }

    private fun navegarAVictoria(palabra: String, sonidoOn: Boolean) {
        val intent = Intent(this, VictoriaActivity::class.java).apply {
            putExtra("palabra_clave", palabra)
            putExtra("SonidoOn-Off", sonidoOn)
        }
        startActivity(intent)
        finish()
    }

    private fun navegarADerrota(palabra: String, sonidoOn: Boolean) {
        val intent = Intent(this, DerrotaActivity::class.java).apply {
            putExtra("palabra_clave", palabra)
            putExtra("SonidoOn-Off", sonidoOn)
        }
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun TableroScreen(
    uiState: TableroUiState,
    onSonidoToggle: () -> Unit,
    onLetraPulsada: (Char) -> Unit
) {
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
                Text(text = uiState.categoria, fontSize = 20.sp)
                IconButton(onClick = onSonidoToggle) {
                    Icon(
                        painter = painterResource(id = if (uiState.sonidoOn) R.drawable.ic_volume_up else R.drawable.ic_volume_off),
                        contentDescription = "Sonido"
                    )
                }
            }

            AhorcadoImage(errores = uiState.errores)

            // La palabra ya viene formateada desde el ViewModel
            PalabraOculta(palabraMostrada = uiState.palabraOculta)

            Teclado(letrasPulsadas = uiState.letrasPulsadas, juegoTerminado = uiState.juegoTerminado, onLetraPulsada = onLetraPulsada)
        }
    }
}

@Composable
fun AhorcadoImage(errores: Int) {
    // Definimos las imágenes para cada estado de error.
    // El primer estado (0 errores) no necesita imagen, así que usamos un Spacer.
    val imagenRes = when (errores) {
        1 -> R.drawable.ic_cuerda
        2 -> R.drawable.ic_cabeza
        3 -> R.drawable.ic_cuerpo
        4 -> R.drawable.ic_brazo
        5 -> R.drawable.ic_brazos
        6 -> R.drawable.ic_pierna // Imagen del último error antes de perder
        else -> null // Para 0 errores o cualquier otro caso
    }

    if (imagenRes != null) {
        // Muestra la imagen correspondiente al número de errores
        Icon(
            painter = painterResource(id = imagenRes),
            contentDescription = "Progreso del ahorcado",
            modifier = Modifier.size(150.dp)
        )
    } else {
        // Si no hay errores (o es un estado inesperado), muestra un espacio
        // para mantener el layout consistente.
        Spacer(modifier = Modifier.size(150.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PalabraOculta(palabraMostrada: String) {
    FlowRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center
    ) {
        palabraMostrada.forEach { char ->
            Text(
                text = char.toString(),
                fontSize = 32.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Teclado(letrasPulsadas: Set<Char>, juegoTerminado: Boolean, onLetraPulsada: (Char) -> Unit) {
    val teclado = "QWERTYUIOPASDFGHJKLZXCVBNM"

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        teclado.forEach { letra ->
            val isEnabled = letra !in letrasPulsadas && !juegoTerminado
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
    AhorcadoApp25Theme {
        TableroScreen(
            uiState = TableroUiState(palabraOculta = "H O L A", categoria = "SALUDOS", sonidoOn = true, errores = 2),
            onSonidoToggle = {},
            onLetraPulsada = {}
        )
    }
}
