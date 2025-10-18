# Proyecto Ahorcado - Versión Refactorizada con Jetpack Compose

Este proyecto es una versión modernizada del juego del Ahorcado de Valeriano Moreno (https://github.com/Valexx55), migrada de la arquitectura tradicional de Vistas XML a **Jetpack Compose**. El objetivo de la refactorización ha sido adoptar las mejores prácticas del desarrollo moderno de Android, mejorando la mantenibilidad, escalabilidad y la experiencia de desarrollo.

## Resumen de Cambios Realizados

El proyecto original, basado en `Activities` y `Layouts XML`, ha sido transformado por completo. Los cambios más significativos incluyen:

### 1. **Migración a Jetpack Compose**
- **Eliminación de Vistas XML:** Todos los archivos de layout (`.xml`) han sido reemplazados por funciones Composable (`@Composable`).
- **Adiós a `findViewById`:** Se ha eliminado por completo el uso de `findViewById`, sustituyéndolo por un flujo de datos declarativo y unidireccional (UDF).
- **UI Moderna:** La interfaz de usuario ahora se construye con componentes modernos de Material 3.

### 2. **Implementación del Patrón de Arquitectura MVVM**
- **Separación de Responsabilidades:** Se ha introducido un `ViewModel` para cada pantalla (`Inicial`, `Categoria`, `Tablero`, `Victoria`/`Derrota`).
    - La **Activity** (`ComponentActivity`) ahora solo se encarga de gestionar el ciclo de vida y la navegación.
    - El **ViewModel** contiene toda la lógica de negocio, gestiona el estado y maneja las acciones del usuario.
    - La **Vista** (Composable) es un reflejo pasivo del estado del ViewModel y le notifica los eventos de usuario.

### 3. **Gestión de Estado Reactiva**
- Se utiliza `StateFlow` y `MutableStateFlow` en los ViewModels para exponer el estado de la UI (`UiState`).
- Las vistas en Jetpack Compose se suscriben a estos flujos de estado usando `collectAsState()`, de modo que la UI se actualiza automáticamente cuando el estado cambia.

### 4. **Navegación Basada en Eventos**
- La navegación entre pantallas se gestiona a través de un canal de eventos (`Channel`) en los ViewModels.
- Las Activities observan estos eventos y son las responsables de construir los `Intents` y lanzar las nuevas pantallas, manteniendo la lógica de navegación fuera de la capa de negocio.

### 5. **Refactorización y Depuración de Lógica**
- **Gestión del `MediaPlayer`:** Se ha centralizado el control del sonido, asociándolo al estado (`musicaOn`) en el ViewModel y manejando su ciclo de vida (`start`, `pause`, `release`) correctamente en la Activity para evitar fugas de memoria y comportamientos inesperados.
- **Lógica del Juego:** Se corrigió un error "off-by-one" que finalizaba la partida antes de tiempo. Ahora, el número de errores permitidos está sincronizado entre la lógica del ViewModel y las imágenes que se muestran en la UI.
- **Detección de Victoria:** Se solucionó un bug en el conteo de letras acertadas para palabras con caracteres repetidos.
- **Manejo de Recursos:** Se ha hecho el código más robusto al prevenir `ResourceResolutionException` (crashes por recursos nulos), asegurando que siempre se proporcione una imagen por defecto.

## Estructura del Proyecto

El proyecto sigue una estructura orientada a funcionalidades, donde cada pantalla principal tiene sus propios componentes:

- **`InicialActivity` / `InicialViewModel`**: Pantalla de bienvenida, inicio del juego y acceso a créditos.
- **`CategoriaActivity` / `CategoriaViewModel`**: Pantalla para la selección de la categoría de palabras.
- **`TableroActivity` / `TableroViewModel`**: El núcleo del juego, donde se muestra la palabra oculta, el teclado y el progreso del ahorcado.
- **`VictoriaActivity` / `DerrotaActivity` / `EndGameViewModel`**: Pantallas de fin de juego que muestran el resultado y permiten volver a jugar o ir al menú.
- **`CreditosActivity`**: Pantalla de créditos con scroll vertical.

## Próximos Pasos y Mejoras Potenciales
- **Inyección de Dependencias:** Integrar Hilt o Koin para gestionar la creación de ViewModels y otras dependencias (como un `SoundManager`).
- **Navegación con Compose Navigation:** Reemplazar los `Intents` entre Activities por un `NavHost` de Compose Navigation para tener una Single-Activity App.
- **Pruebas Unitarias:** Añadir tests unitarios para los ViewModels para verificar la lógica del juego de forma aislada.