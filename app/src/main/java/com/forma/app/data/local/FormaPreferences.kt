package com.forma.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.forma.app.domain.repository.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "forma_prefs")

@Singleton
class FormaPreferences @Inject constructor(
    private val context: Context,
) {
    private object Keys {
        val UID = stringPreferencesKey("uid")
        val EMAIL = stringPreferencesKey("email")
        val NAME = stringPreferencesKey("name")
        val PHOTO = stringPreferencesKey("photo")
        val PROVIDER = stringPreferencesKey("provider")
        val REST_SECONDS = intPreferencesKey("rest_seconds")
        val REMINDERS = booleanPreferencesKey("reminders")
    }

    val user: Flow<AuthUser?> = context.dataStore.data.map { prefs ->
        val uid = prefs[Keys.UID] ?: return@map null
        AuthUser(
            uid = uid,
            email = prefs[Keys.EMAIL].orEmpty(),
            displayName = prefs[Keys.NAME].orEmpty(),
            photoUrl = prefs[Keys.PHOTO],
            provider = prefs[Keys.PROVIDER] ?: "local",
        )
    }

    val restSeconds: Flow<Int> = context.dataStore.data.map { it[Keys.REST_SECONDS] ?: 90 }

    val remindersEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.REMINDERS] ?: true }

    suspend fun saveUser(user: AuthUser) {
        context.dataStore.edit { prefs ->
            prefs[Keys.UID] = user.uid
            prefs[Keys.EMAIL] = user.email
            prefs[Keys.NAME] = user.displayName
            prefs[Keys.PROVIDER] = user.provider
            user.photoUrl?.let { prefs[Keys.PHOTO] = it } ?: prefs.remove(Keys.PHOTO)
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.UID)
            prefs.remove(Keys.EMAIL)
            prefs.remove(Keys.NAME)
            prefs.remove(Keys.PHOTO)
            prefs.remove(Keys.PROVIDER)
        }
    }

    suspend fun setRestSeconds(seconds: Int) {
        context.dataStore.edit { it[Keys.REST_SECONDS] = seconds }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.REMINDERS] = enabled }
    }
}
