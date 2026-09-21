# Firebase — FORMA

La app ya sincroniza perfil, rutinas, nutrición, chat, artículos guardados, comunidad y fotos
cuando existe `app/google-services.json`. Sin ese archivo sigue funcionando solo con Room.

## 1. Crear el proyecto

1. Entra en [Firebase Console](https://console.firebase.google.com/) y crea un proyecto (o usa uno).
2. Añade una app Android con package name `com.forma.app`.
3. Descarga `google-services.json` y colócalo en `app/google-services.json`.
4. Activa **Authentication** → Email/Password (y Google si quieres).
5. Crea una base **Firestore** (modo producción) y un bucket de **Storage**.

## 2. Desplegar reglas

Desde la raíz del repo (con [Firebase CLI](https://firebase.google.com/docs/cli)):

```bash
firebase login
firebase init  # elige Firestore + Storage; no hace falta sobrescribir las reglas si apuntas a firebase/
firebase deploy --only firestore:rules,storage
```

Las reglas del repo están en:

- `firebase/firestore.rules`
- `firebase/storage.rules`

## 3. Esquema

```
users/{uid}                          perfil
users/{uid}/exercise_logs/{id}       ejercicios marcados
users/{uid}/workout_sessions/{id}    sesiones terminadas
users/{uid}/meal_choices/{id}        comidas elegidas + foto
users/{uid}/chat_messages/{id}       historial del asistente
users/{uid}/article_states/{id}      artículos guardados
users/{uid}/following/{authorId}     follows
users/{uid}/engagement/{postId}      likes/reacciones propias
posts/{postId}                       feed público
authors/{authorId}                   perfiles públicos
Storage: users/{uid}/{profile|workouts|plates|posts}/…
```

## 4. Comportamiento en la app

- Room es la caché offline; la UI no cambia.
- Al iniciar sesión se hace **pull** nube → Room.
- Cada guardado hace **push** Room → nube (si Firebase está activo).
- Las fotos locales se suben a Storage y se guarda la URL https.
