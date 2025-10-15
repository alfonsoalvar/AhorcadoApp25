package antonio.femxa.appfinal

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import antonio.femxa.appfinal.ui.theme.AhorcadoApp25Theme

class CategoriaActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null
    var musicaOnOff: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        musicaOnOff = intent.getBooleanExtra("SonidoOn-Off", false)

        mediaPlayer = MediaPlayer.create(this, R.raw.inicio)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(100f, 100f)

        setContent {
            AhorcadoApp25Theme {
                CategoriaScreen(
                    musicaOn = musicaOnOff,
                    onCategorySelected = { categoria, palabra ->
                        val intent = Intent(this, TableroActivity::class.java).apply {
                            putExtra("palabra_clave", palabra)
                            putExtra("categoria_seleccionada", categoria)
                            putExtra("SonidoOn-Off", musicaOnOff)
                        }
                        startActivity(intent)
                    },
                    onSoundToggle = {
                        musicaOnOff = !musicaOnOff
                        if (mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.pause()
                        } else {
                            mediaPlayer?.start()
                        }
                    }
                )
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@CategoriaActivity, InicialActivity::class.java).apply {
                    putExtra("SonidoOn-Off", musicaOnOff)
                }
                startActivity(intent)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (musicaOnOff) {
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun CategoriaScreen(
    musicaOn: Boolean,
    onCategorySelected: (String, String) -> Unit,
    onSoundToggle: () -> Unit
) {
    val context = LocalContext.current
    val categories = stringArrayResource(R.array.categorias).toList()
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories.getOrElse(0) { "Selecciona categoría" }) }
    var musicaState by remember { mutableStateOf(musicaOn) }

    fun palabraOculta(array_especifico: Array<CharSequence>): String {
        val aleatoria = (Math.random() * array_especifico.size).toInt()
        Log.d("MENSAJE2", aleatoria.toString() + " " + array_especifico.size)
        return array_especifico[aleatoria].toString()
    }

    fun getWordForCategory(pos: Int): String {
        if (pos == 0) return ""
        val array_categorias = context.resources.obtainTypedArray(R.array.array_categorias)
        val array_especifico = array_categorias.getTextArray(pos)
        array_categorias.recycle()
        val palabra = palabraOculta(array_especifico)
        Log.d("MENSAJE2", palabra)
        return palabra
    }

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
                Button(onClick = { expanded = !expanded }) {
                    Text(selectedCategory)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEachIndexed { index, category ->
                        DropdownMenuItem(
                            text = { Text(text = category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                                if (index != 0) {
                                    val palabra = getWordForCategory(index)
                                    onCategorySelected(category, palabra)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            IconButton(onClick = {
                musicaState = !musicaState
                onSoundToggle()
            }) {
                Icon(
                    painter = painterResource(id = if (musicaState) R.drawable.ic_volume_up else R.drawable.ic_volume_off),
                    contentDescription = "Toggle Sound"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriaPreview() {
    CategoriaScreen(musicaOn = false, onCategorySelected = { _, _ -> }, onSoundToggle = {})
}
