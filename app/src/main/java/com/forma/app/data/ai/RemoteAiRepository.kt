package com.forma.app.data.ai

import android.util.Log
import com.forma.app.BuildConfig
import com.forma.app.domain.model.ChatMessage
import com.forma.app.domain.model.MealSlot
import com.forma.app.domain.model.Recipe
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.model.WeeklyRoutine
import com.forma.app.domain.repository.AiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Punto de conexión con un proveedor de IA real (API compatible con chat completions).
 *
 * Es el único lugar que hay que tocar para pasar de la IA local a una de verdad:
 * define `FORMA_AI_API_KEY` en `local.properties` (o como variable de entorno) y cambia el
 * binding de [com.forma.app.di.AiModule] a esta clase.
 *
 * Mientras no haya API key —o si la llamada falla— delega en [LocalAiRepository], de modo que la
 * app nunca se queda sin respuesta.
 */
@Singleton
class RemoteAiRepository @Inject constructor(
    private val local: LocalAiRepository,
) : AiRepository {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .build()
    }

    private val json = Json { ignoreUnknownKeys = true }

    private val hasCredentials: Boolean get() = BuildConfig.AI_API_KEY.isNotBlank()

    override val providerName: String
        get() = if (hasCredentials) "Proveedor remoto (${BuildConfig.AI_MODEL})" else local.providerName

    // La generación estructurada (rutina y dieta) se mantiene local por ahora: garantiza
    // ejercicios y recetas válidos. El siguiente paso es pedir JSON con el mismo esquema.
    override suspend fun generateWeeklyRoutine(profile: UserProfile): WeeklyRoutine =
        local.generateWeeklyRoutine(profile)

    override suspend fun generateDayMeals(
        profile: UserProfile,
        day: WeekDay,
    ): Map<MealSlot, List<Recipe>> = local.generateDayMeals(profile, day)

    override suspend fun answer(
        profile: UserProfile?,
        history: List<ChatMessage>,
        question: String,
    ): String {
        if (!hasCredentials) return local.answer(profile, history, question)
        return runCatching { requestCompletion(profile, history, question) }
            .getOrElse { error ->
                Log.w(TAG, "Fallo la llamada remota, se responde en local", error)
                local.answer(profile, history, question)
            }
    }

    private suspend fun requestCompletion(
        profile: UserProfile?,
        history: List<ChatMessage>,
        question: String,
    ): String = withContext(Dispatchers.IO) {
        val payload = buildJsonObject {
            put("model", BuildConfig.AI_MODEL)
            put("temperature", 0.6)
            put("messages", buildMessages(profile, history, question))
        }

        val request = Request.Builder()
            .url("${BuildConfig.AI_BASE_URL.trimEnd('/')}/chat/completions")
            .addHeader("Authorization", "Bearer ${BuildConfig.AI_API_KEY}")
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            check(response.isSuccessful) { "HTTP ${response.code}: $body" }
            json.parseToJsonElement(body)
                .jsonObject["choices"]!!.jsonArray
                .first().jsonObject["message"]!!.jsonObject["content"]!!
                .jsonPrimitive.content
                .trim()
        }
    }

    private fun buildMessages(
        profile: UserProfile?,
        history: List<ChatMessage>,
        question: String,
    ): JsonArray = buildJsonArray {
        add(
            buildJsonObject {
                put("role", "system")
                put("content", systemPrompt(profile))
            },
        )
        history.takeLast(10).forEach { message ->
            add(
                buildJsonObject {
                    put("role", if (message.fromUser) "user" else "assistant")
                    put("content", message.text)
                },
            )
        }
        add(
            buildJsonObject {
                put("role", "user")
                put("content", question)
            },
        )
    }

    private fun systemPrompt(profile: UserProfile?): String = buildString {
        append("Eres el asistente de FORMA, una app de deporte y nutrición. ")
        append("Responde SIEMPRE en español, claro y directo, sin emojis innecesarios. ")
        append("No des diagnósticos médicos; si hay dolor persistente sugiere acudir a un profesional.\n\n")
        if (profile != null) {
            append("Datos del usuario:\n")
            append("- Nombre: ${profile.name}\n")
            append("- Edad: ${profile.age} años\n")
            append("- Peso: ${profile.weightKg} kg, estatura: ${profile.heightCm} cm, IMC: ${profile.bmi}\n")
            append("- Deporte: ${profile.sport.displayName}, nivel: ${profile.level.displayName}\n")
            append("- Objetivo: ${profile.goal.displayName} (~${profile.dailyCalories} kcal/día)\n")
            append("- Equipo disponible: ${profile.equipment.joinToString().ifBlank { "sin especificar" }}\n")
            append("- Ingredientes preferidos: ${profile.likedIngredients.joinToString().ifBlank { "sin especificar" }}\n")
        }
    }

    private companion object {
        const val TAG = "RemoteAiRepository"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
