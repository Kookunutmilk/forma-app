package com.forma.app.data.repository

import com.forma.app.data.local.FormaPreferences
import com.forma.app.domain.repository.AuthRepository
import com.forma.app.domain.repository.AuthUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

private val EMAIL_REGEX = Regex("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$")

private fun validate(email: String, password: String): String? = when {
    email.isBlank() -> "Escribe tu correo electrónico."
    !EMAIL_REGEX.matches(email.trim()) -> "Ese correo no se ve válido."
    password.length < 6 -> "La contraseña necesita al menos 6 caracteres."
    else -> null
}

/**
 * Autenticación local persistida en DataStore. Permite usar la app completa sin
 * `google-services.json`, manteniendo la misma interfaz que la implementación de Firebase.
 */
@Singleton
class LocalAuthRepository @Inject constructor(
    private val preferences: FormaPreferences,
) : AuthRepository {

    override val currentUser: Flow<AuthUser?> = preferences.user

    override val providerName: String = "Cuenta local"

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> {
        delay(500)
        validate(email, password)?.let { return Result.failure(IllegalArgumentException(it)) }
        val clean = email.trim().lowercase(Locale.ROOT)
        val user = AuthUser(
            uid = "local_${clean.hashCode().absoluteValue}",
            email = clean,
            displayName = nameFromEmail(clean),
        )
        preferences.saveUser(user)
        return Result.success(user)
    }

    override suspend fun signUpWithEmail(
        name: String,
        email: String,
        password: String,
    ): Result<AuthUser> {
        delay(600)
        if (name.trim().length < 2) {
            return Result.failure(IllegalArgumentException("Escribe tu nombre real."))
        }
        validate(email, password)?.let { return Result.failure(IllegalArgumentException(it)) }
        val clean = email.trim().lowercase(Locale.ROOT)
        val user = AuthUser(
            uid = "local_${clean.hashCode().absoluteValue}",
            email = clean,
            displayName = name.trim(),
        )
        preferences.saveUser(user)
        return Result.success(user)
    }

    override suspend fun signInWithGoogle(): Result<AuthUser> {
        delay(700)
        val user = AuthUser(
            uid = "google_demo",
            email = "frida.gonzalez@gmail.com",
            displayName = "Frida González",
            provider = "google",
        )
        preferences.saveUser(user)
        return Result.success(user)
    }

    override suspend fun signOut() {
        preferences.clearUser()
    }

    private fun nameFromEmail(email: String): String =
        email.substringBefore("@")
            .split(".", "_", "-")
            .filter { it.isNotBlank() }
            .joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }
}

/**
 * Autenticación real con Firebase. Se activa sola cuando el proyecto tiene
 * `app/google-services.json` (ver [com.forma.app.di.AuthModule]).
 */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val preferences: FormaPreferences,
) : AuthRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override val currentUser: Flow<AuthUser?> = preferences.user

    override val providerName: String = "Firebase Auth"

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> {
        validate(email, password)?.let { return Result.failure(IllegalArgumentException(it)) }
        return runCatching {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            result.user!!.toAuthUser().also { preferences.saveUser(it) }
        }
    }

    override suspend fun signUpWithEmail(
        name: String,
        email: String,
        password: String,
    ): Result<AuthUser> {
        validate(email, password)?.let { return Result.failure(IllegalArgumentException(it)) }
        return runCatching {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user!!.toAuthUser().copy(displayName = name.trim())
            preferences.saveUser(user)
            user
        }
    }

    /**
     * El inicio con Google necesita el flujo de Credential Manager desde una Activity y un
     * `default_web_client_id` proveniente de `google-services.json`. Con la configuración
     * ausente devolvemos un error explícito en vez de fingir una sesión.
     */
    override suspend fun signInWithGoogle(): Result<AuthUser> = Result.failure(
        IllegalStateException(
            "Falta configurar el cliente web de Google en google-services.json.",
        ),
    )

    override suspend fun signOut() {
        auth.signOut()
        preferences.clearUser()
    }

    /** Si Firebase ya tiene sesión y DataStore no, la recupera. */
    suspend fun restoreSessionIfNeeded() {
        val firebaseUser = auth.currentUser ?: return
        if (preferences.user.first() == null) {
            preferences.saveUser(firebaseUser.toAuthUser())
        }
    }

    private fun com.google.firebase.auth.FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName.orEmpty().ifBlank { email?.substringBefore("@").orEmpty() },
        photoUrl = photoUrl?.toString(),
        provider = "firebase",
    )
}
