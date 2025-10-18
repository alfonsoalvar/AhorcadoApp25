package antonio.femxa.appfinal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EndGameUiState(
    val palabra: String = "",
    val musicaOn: Boolean = false
)

class EndGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EndGameUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun initialize(palabra: String, musicaOn: Boolean) {
        _uiState.value = EndGameUiState(palabra = palabra, musicaOn = musicaOn)
    }

    fun onPlayAgainClicked() {
        viewModelScope.launch {
            _navigationEvent.send(NavigationEvent.NavigateToCategoria(_uiState.value.musicaOn))
        }
    }

    fun onMainMenuClicked() {
        viewModelScope.launch {
            _navigationEvent.send(NavigationEvent.NavigateToMenu(_uiState.value.musicaOn))
        }
    }

    fun onShareClicked() {
        val message = "¡Gané en el juego del ahorcado! La palabra era ${_uiState.value.palabra}. ¿Puedes superarlo?"
        viewModelScope.launch {
            _navigationEvent.send(NavigationEvent.Share(message))
        }
    }
}