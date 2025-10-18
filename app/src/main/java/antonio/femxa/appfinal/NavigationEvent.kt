package antonio.femxa.appfinal

sealed class NavigationEvent {
    // Navigation targets
    data class NavigateToCategoria(val musicaOn: Boolean) : NavigationEvent() // For "Play" and "Play Again"
    object NavigateToCreditos : NavigationEvent()
    data class NavigateToTablero(val palabra: String, val categoria: String, val musicaOn: Boolean) : NavigationEvent()
    data class NavigateToVictoria(val palabra: String, val musicaOn: Boolean) : NavigationEvent()
    data class NavigateToDerrota(val palabra: String, val musicaOn: Boolean) : NavigationEvent()
    data class NavigateToMenu(val musicaOn: Boolean) : NavigationEvent() // For going back to the main menu

    // Other Actions (Side Effects)
    data class Share(val message: String) : NavigationEvent()
}
