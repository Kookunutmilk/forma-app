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
│   ├── remote/      FormaCloudStore (NoOp o Firebase Firestore/Storage)
│   ├── catalog/     catálogos en español: ejercicios, plantillas por deporte, recetas, artículos
│   ├── ai/          LocalAiRepository (determinista) y RemoteAiRepository (proveedor real)
│   └── repository/  implementaciones de los repositorios (Room-first + sync a la nube)
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

### Autenticación y nube (Firebase)

Sin `app/google-services.json` la app usa auth local (DataStore) y **solo Room**.

Con `google-services.json` en `app/`:

1. Se activa Firebase Auth.
2. Room sigue siendo la caché offline.
3. Perfil, rutinas, nutrición, chat, artículos guardados, comunidad y fotos se sincronizan con
   **Cloud Firestore** y **Storage**.

Pasos completos (proyecto, reglas, esquema): ver [`firebase/README.md`](firebase/README.md).

## Presentación, guión y reporte

Los PDF están en [`docs/presentacion/pdf/`](docs/presentacion/pdf/):

- [FORMA-presentacion.pdf](docs/presentacion/pdf/FORMA-presentacion.pdf) — 16 diapositivas, una por página.
- [FORMA-presentacion-con-capturas.pdf](docs/presentacion/pdf/FORMA-presentacion-con-capturas.pdf) — el mismo relato, con las pantallas de la app.
- [FORMA-guion.pdf](docs/presentacion/pdf/FORMA-guion.pdf) — qué decir en cada diapositiva.
- [FORMA-reporte.pdf](docs/presentacion/pdf/FORMA-reporte.pdf) — funciones, motor, datos y pruebas.

Las mismas piezas se pueden abrir en el navegador desde [`docs/presentacion/`](docs/presentacion/index.html) (flechas, notas con `N`, índice con `O`). La versión web incluye un laboratorio en vivo de la rutina, las calorías y el coach.

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

- **Sin Firebase:** todo vive en el dispositivo (Room + DataStore + fotos en almacenamiento interno).
- **Con Firebase:** Room es caché offline; los datos de cada usuario van a `users/{uid}/…` y el feed
  público a `posts/`. Las fotos se suben a Storage. Las reglas en `firebase/` limitan lectura/escritura
  al dueño (excepto el feed público).
