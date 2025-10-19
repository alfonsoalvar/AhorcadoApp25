package antonio.femxa.appfinal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoriaUiState(
    val musicaOn: Boolean = false,
    val expanded: Boolean = false,
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "Elige categoría",
    val wordArrays: List<List<String>> = emptyList()
)

class CategoriaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun init(musicaOn: Boolean, categories: List<String>, wordArrays: List<List<String>>) {
        _uiState.value = CategoriaUiState(
            musicaOn = musicaOn,
            categories = categories,
            selectedCategory = categories.firstOrNull() ?: "Elige categoría",
            wordArrays = wordArrays
        )
    }

    fun onCategorySelected(category: String, index: Int) {
        _uiState.update { it.copy(selectedCategory = category, expanded = false) }

        Log.d("CategoriaViewModel",  _uiState.value.wordArrays.toString())

        val palabrasDeCategoria = _uiState.value.wordArrays.getOrNull(index - 1)
        Log.d("CategoriaViewModel", palabrasDeCategoria.toString())
        if (palabrasDeCategoria.isNullOrEmpty()) {
            // Opcional: manejar el caso de que no haya palabras para la categoría
            return
        }

        val palabraOculta = palabrasDeCategoria.random()
        Log.d("CategoriaViewModel", palabraOculta)

        viewModelScope.launch {
            _navigationEvent.send(
                NavigationEvent.NavigateToTablero(
                    palabra = palabraOculta,
                    categoria = category,
                    musicaOn = _uiState.value.musicaOn
                )
            )
        }
    }

    fun onSoundToggle() {
        _uiState.update { it.copy(musicaOn = !it.musicaOn) }
    }

    fun onDropdownToggle(isExpanded: Boolean) {
        _uiState.update { it.copy(expanded = isExpanded) }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _navigationEvent.send(NavigationEvent.NavigateToMenu(_uiState.value.musicaOn))
        }
    }
}