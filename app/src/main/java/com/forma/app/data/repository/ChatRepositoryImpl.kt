package com.forma.app.data.repository

import com.forma.app.data.ai.LocalCoach
import com.forma.app.data.local.dao.ChatDao
import com.forma.app.data.local.entity.ChatMessageEntity
import com.forma.app.domain.model.ChatMessage
import com.forma.app.domain.repository.AiRepository
import com.forma.app.domain.repository.ChatRepository
import com.forma.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val dao: ChatDao,
    private val ai: AiRepository,
    private val profiles: ProfileRepository,
) : ChatRepository {

    override val suggestedQuestions: List<String> = LocalCoach.suggestedQuestions

    override fun messages(): Flow<List<ChatMessage>> =
        dao.observeMessages().map { list -> list.map { it.toDomain() } }

    override suspend fun send(text: String) {
        val now = System.currentTimeMillis()
        dao.insert(
            ChatMessageEntity(
                id = "msg_${UUID.randomUUID()}",
                text = text,
                fromUser = true,
                timestampMillis = now,
            ),
        )
        val history = dao.observeMessages().first().map { it.toDomain() }
        val profile = profiles.current()
        val reply = ai.answer(profile, history, text)
        dao.insert(
            ChatMessageEntity(
                id = "msg_${UUID.randomUUID()}",
                text = reply,
                fromUser = false,
                timestampMillis = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun clear() = dao.clear()

    private fun ChatMessageEntity.toDomain() = ChatMessage(
        id = id,
        text = text,
        fromUser = fromUser,
        timestampMillis = timestampMillis,
    )
}
