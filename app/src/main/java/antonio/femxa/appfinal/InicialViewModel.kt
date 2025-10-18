package antonio.femxa.appfinal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InicialUiState(
    val musicaOn: Boolean = false
)

class InicialViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InicialUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun setInitialMusicState(musicaOn: Boolean) {
        _uiState.value = InicialUiState(musicaOn = musicaOn)
    }

    fun onJugarClicked() {
        viewModelScope.launch {
            _navigationEvent.send(NavigationEvent.NavigateToCategoria(_uiState.value.musicaOn))
        }
    }

    fun onCreditosClicked() {
        viewModelScope.launch {
            _navigationEvent.send(NavigationEvent.NavigateToCreditos)
        }
    }

    fun onSonidoClicked() {
        _uiState.update { it.copy(musicaOn = !it.musicaOn) }
    }
}