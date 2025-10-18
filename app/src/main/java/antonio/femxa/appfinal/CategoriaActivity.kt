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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import antonio.femxa.appfinal.ui.theme.AhorcadoApp25Theme
import kotlinx.coroutines.flow.collectLatest

class CategoriaActivity : ComponentActivity() {

    private val viewModel: CategoriaViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Cargar todos los datos iniciales desde la Activity
        val musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", false)
        val categories = resources.getStringArray(R.array.categorias).toList()
        val wordArrays = listOf(
            resources.getStringArray(R.array.animales).toList(),
            resources.getStringArray(R.array.deportes).toList(),
            resources.getStringArray(R.array.eñe_palabras).toList(),
            resources.getStringArray(R.array.estilosmusicales).toList(),
            resources.getStringArray(R.array.famosos).toList(),
            resources.getStringArray(R.array.fruta).toList(),
            resources.getStringArray(R.array.lugares).toList(),
            resources.getStringArray(R.array.peliculas).toList(),
            resources.getStringArray(R.array.internet).toList()
        )

        // 2. Pasarlos UNA SOLA VEZ al ViewModel
        viewModel.init(musicaOnOff, categories, wordArrays)

        mediaPlayer = MediaPlayer.create(this, R.raw.inicio).apply {
            isLooping = true
            setVolume(100f, 100f)
        }

        setContent {
            // 3. La UI se suscribe al estado del ViewModel
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
                        is NavigationEvent.NavigateToTablero -> {
                            val intent = Intent(this@CategoriaActivity, TableroActivity::class.java).apply {
                                putExtra("palabra_clave", event.palabra)
                                putExtra("categoria_seleccionada", event.categoria)
                                putExtra("SonidoOn-Off", event.musicaOn)
                            }
                            startActivity(intent)
                        }
                        is NavigationEvent.NavigateToMenu -> {
                            val intent = Intent(this@CategoriaActivity, InicialActivity::class.java).apply {
                                putExtra("SonidoOn-Off", event.musicaOn)
                            }
                            startActivity(intent)
                        }
                        else -> Log.d("CategoriaActivity", "Unhandled navigation event: $event")
                    }
                }
            }

            AhorcadoApp25Theme {
                CategoriaScreen(
                    uiState = uiState,
                    onCategorySelected = viewModel::onCategorySelected,
                    onSoundToggle = viewModel::onSoundToggle,
                    onDropdownToggle = viewModel::onDropdownToggle
                )
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onBackPressed()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uiState.value.musicaOn && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun CategoriaScreen(
    uiState: CategoriaUiState,
    onCategorySelected: (String, Int) -> Unit,
    onSoundToggle: () -> Unit,
    onDropdownToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Selecciona una categoría")
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                Button(onClick = { onDropdownToggle(!uiState.expanded) }) {
                    Text(uiState.selectedCategory)
                }
                DropdownMenu(
                    expanded = uiState.expanded,
                    onDismissRequest = { onDropdownToggle(false) }
                ) {
                    uiState.categories.forEachIndexed { index, category ->
                        DropdownMenuItem(
                            text = { Text(text = category) },
                            onClick = { onCategorySelected(category, index) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            IconButton(onClick = onSoundToggle) {
                Icon(
                    painter = painterResource(id = if (uiState.musicaOn) R.drawable.ic_volume_up else R.drawable.ic_volume_off),
                    contentDescription = "Toggle Sound"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriaPreview() {
    AhorcadoApp25Theme {
        CategoriaScreen(
            uiState = CategoriaUiState(
                categories = listOf("Ciencia", "Deportes", "Geografía"),
                selectedCategory = "Ciencia"
            ),
            onCategorySelected = { _, _ -> },
            onSoundToggle = {},
            onDropdownToggle = {}
        )
    }
}
