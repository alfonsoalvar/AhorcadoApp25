package antonio.femxa.appfinal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TableroUiState(
    val palabraOriginal: String = "",
    val palabraOculta: String = "",
    val categoria: String = "",
    val letrasPulsadas: Set<Char> = emptySet(),
    val errores: Int = 0,
    val sonidoOn: Boolean = false,
    val juegoTerminado: Boolean = false,
    val resultado: JuegoResultado? = null
)

enum class JuegoResultado {
    VICTORIA,
    DERROTA
}

class TableroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TableroUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun iniciarJuego(palabra: String, categoria: String, sonidoOn: Boolean) {
        val palabraNormalizada = palabra.uppercase().replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
        _uiState.value = TableroUiState(
            palabraOriginal = palabraNormalizada,
            palabraOculta = generarPalabraOculta(palabraNormalizada),
            categoria = categoria,
            sonidoOn = sonidoOn
        )
    }

    fun onLetraPulsada(letra: Char) {
        if (_uiState.value.juegoTerminado) return

        val letraUpper = letra.uppercaseChar()
        val nuevasLetras = _uiState.value.letrasPulsadas + letraUpper

        if (letraUpper in _uiState.value.palabraOriginal) {
            val nuevaPalabraOculta = generarPalabraOculta(_uiState.value.palabraOriginal, nuevasLetras)
            _uiState.update { it.copy(letrasPulsadas = nuevasLetras, palabraOculta = nuevaPalabraOculta) }
            comprobarVictoria()
        } else {
            val nuevosErrores = _uiState.value.errores + 1
            _uiState.update { it.copy(letrasPulsadas = nuevasLetras, errores = nuevosErrores) }
            comprobarDerrota(nuevosErrores)
        }
    }

    private fun generarPalabraOculta(palabra: String, letrasAcertadas: Set<Char> = emptySet()): String {
        return palabra.map { if (it in letrasAcertadas || it == ' ') it else '_' }.joinToString(" ")
    }

    private fun comprobarVictoria() {
        if (!_uiState.value.palabraOculta.contains('_')) {
            _uiState.update { it.copy(juegoTerminado = true, resultado = JuegoResultado.VICTORIA) }
            viewModelScope.launch {
                _navigationEvent.send(NavigationEvent.NavigateToVictoria(_uiState.value.palabraOriginal, _uiState.value.sonidoOn))
            }
        }
    }

    private fun comprobarDerrota(errores: Int) {
        if (errores >= 7) { // errores máximo
            _uiState.update { it.copy(juegoTerminado = true, resultado = JuegoResultado.DERROTA) }
            viewModelScope.launch {
                _navigationEvent.send(NavigationEvent.NavigateToDerrota(_uiState.value.palabraOriginal, _uiState.value.sonidoOn))
            }
        }
    }

    fun onSonidoToggle() {
        _uiState.update { it.copy(sonidoOn = !it.sonidoOn) }
    }
}
