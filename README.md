# FORMA

App Android nativa de entrenamiento y nutrición personalizada. FORMA arma tu rutina semanal y tu
plan de comidas a partir de tu perfil (deporte, nivel, objetivo, equipo disponible y alimentos que
te gustan), te acompaña durante la sesión, te deja compartir tu avance con una comunidad sin
comentarios tóxicos y resuelve tus dudas con un asistente de IA.

Toda la interfaz está en español.

## Qué incluye

Seis pestañas, todas funcionales con datos de ejemplo realistas:

| Pestaña | Qué hace |
| --- | --- |
| **Inicio** | Saludo, tira de la semana, métricas (entrenos, racha, kcal), el entreno de hoy, IMC, accesos rápidos y artículos recomendados. |
| **Rutina** | Rutina semanal generada para tu deporte y equipo. Marcas ejercicios, corre un cronómetro de descanso y al terminar subes tu foto. |
| **Dieta** | Plan diario con 5 opciones por comida, macros por receta y por día, detalle con ingredientes y pasos, y foto opcional del plato. |
| **Comunidad** | Feed tipo Instagram filtrable por deporte: like, reacciones con emoji, compartir y seguir. Sin comentarios, por diseño. |
| **IA** | Asistente de fitness con preguntas frecuentes y chat que responde usando tu perfil (peso, objetivo, deporte). |
| **Aprender** | Catálogo de artículos por categoría, con buscador, guardados y lectura completa. |

Además: autenticación con correo o Google, onboarding de cinco pasos y perfil editable con
publicaciones, ajustes de descanso y recordatorios.

## Arquitectura

Un solo módulo `:app` con capas separadas por paquete, MVVM y flujo unidireccional de estado
(`StateFlow` → `collectAsStateWithLifecycle` → Compose).

```
com.forma.app
├── core/            utilidades (persistencia de imágenes elegidas por el usuario)
├── designsystem/    tema oscuro + acento lima, tipografía y componentes reutilizables
├── domain/
│   ├── model/       modelos puros: UserProfile, WeeklyRoutine, DayMealPlan, Post, Article…
│   └── repository/  interfaces: Auth, Profile, Training, Nutrition, Community, Learn, Chat, Ai
├── data/
│   ├── local/       Room (entidades, DAOs, base) y DataStore
│   ├── catalog/     catálogos en español: ejercicios, plantillas por deporte, recetas, artículos
│   ├── ai/          LocalAiRepository (determinista) y RemoteAiRepository (proveedor real)
│   └── repository/  implementaciones de los repositorios
├── di/              módulos de Hilt
└── ui/              navigation/ auth/ onboarding/ home/ routine/ diet/ community/ ai/ learn/ profile/
```

**Stack**: Kotlin, Jetpack Compose (Material 3), Hilt, Navigation Compose, Room, DataStore, Coil,
Gradle con Kotlin DSL y version catalog (`gradle/libs.versions.toml`).

### IA sin API key

`AiRepository` es la única puerta a la IA: genera la rutina semanal, las opciones de comida y las
respuestas del chat. Por omisión Hilt inyecta `LocalAiRepository`, una implementación determinista
que funciona sin red ni credenciales. Para conectar un proveedor real (API compatible con OpenAI)
basta con cambiar el binding en `di/AppModule.kt` a `RemoteAiRepository` y definir en
`local.properties`:

```properties
FORMA_AI_API_KEY=sk-...
FORMA_AI_BASE_URL=https://api.openai.com/v1
FORMA_AI_MODEL=gpt-4o-mini
```

`RemoteAiRepository` cae de vuelta al motor local si falta la llave o la llamada falla, así que la
app nunca se queda sin respuesta.

### Autenticación sin Firebase

`AuthRepository` tiene dos implementaciones: `LocalAuthRepository` (persistida en DataStore, la que
corre por omisión) y `FirebaseAuthRepository`. El módulo de Hilt elige una u otra según
`BuildConfig.HAS_FIREBASE`, que a su vez depende de si existe `app/google-services.json`. Coloca ese
archivo en `app/` y el plugin de Google Services y Firebase Auth se activan solos; sin él la app
compila y corre igual.

## Cómo abrirlo en Android Studio

1. Necesitas Android Studio Ladybug (2024.2) o posterior y JDK 17.
2. `File > Open…` y selecciona la carpeta raíz del repositorio.
3. Deja que Gradle sincronice. El wrapper (`gradlew`) trae Gradle 8.11.1; el SDK 35 y las
   build-tools se instalan desde el SDK Manager si faltan.
4. Ejecuta la configuración **app** en un emulador o dispositivo con Android 8.0 (API 26) o
   superior.

### Desde la línea de comandos

```bash
./gradlew assembleDebug                       # genera app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug                        # instala en el dispositivo conectado
./gradlew test                                # pruebas unitarias
```

Si el SDK no está en la ruta por defecto, crea `local.properties` con `sdk.dir=/ruta/al/sdk`.

## Datos y privacidad

Todo vive en el dispositivo: Room guarda perfil, progreso, sesiones, elecciones de comida,
publicaciones, chat y artículos guardados; DataStore guarda la sesión y las preferencias. Las fotos
que eliges se copian al almacenamiento interno de la app (`ImageStore`) para que sigan visibles
después de que caduque el permiso temporal del selector del sistema. No hay backend ni telemetría.
