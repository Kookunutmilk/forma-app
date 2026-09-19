package com.forma.app.data.ai

import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.Sport
import com.forma.app.domain.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalCoachTest {

    private val profile = UserProfile(
        id = "u_test",
        name = "Frida González",
        email = "frida@forma.app",
        weightKg = 60,
        sport = Sport.GYM,
        goal = Goal.GAIN_MUSCLE,
        onboardingCompleted = true,
    )

    @Test
    fun `usa el peso real del perfil para calcular la proteina`() {
        val answer = LocalCoach.answer(profile, "¿Cuántas proteínas necesito al día?")
        assertTrue("No menciona el peso del perfil", answer.contains("60 kg"))
        assertTrue("No da el rango 1.6 a 2.2", answer.contains("1.6") && answer.contains("2.2"))
    }

    @Test
    fun `responde igual con o sin acentos`() {
        assertEquals(
            LocalCoach.answer(profile, "¿Cuántas proteínas necesito al día?"),
            LocalCoach.answer(profile, "cuantas proteinas necesito al dia"),
        )
    }

    @Test
    fun `nunca devuelve una respuesta vacia`() {
        val questions = LocalCoach.suggestedQuestions + listOf(
            "¿Qué hago si me duele la espalda?",
            "asdfgh",
            "",
        )
        questions.forEach { question ->
            val answer = LocalCoach.answer(profile, question)
            assertTrue("Respuesta vacía para: $question", answer.isNotBlank())
        }
    }

    @Test
    fun `funciona sin perfil para poder responder antes del onboarding`() {
        val answer = LocalCoach.answer(null, "¿Cuántas proteínas necesito al día?")
        assertTrue(answer.isNotBlank())
    }

    @Test
    fun `ajusta el consejo de proteina cuando el objetivo es bajar grasa`() {
        val cutting = LocalCoach.answer(profile.copy(goal = Goal.LOSE_FAT), "proteínas al día")
        val bulking = LocalCoach.answer(profile, "proteínas al día")
        assertTrue("No personaliza por objetivo", cutting != bulking)
    }
}
