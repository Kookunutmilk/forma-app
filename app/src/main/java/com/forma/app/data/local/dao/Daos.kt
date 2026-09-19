package com.forma.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.forma.app.data.local.entity.ArticleStateEntity
import com.forma.app.data.local.entity.AuthorEntity
import com.forma.app.data.local.entity.ChatMessageEntity
import com.forma.app.data.local.entity.ExerciseLogEntity
import com.forma.app.data.local.entity.MealChoiceEntity
import com.forma.app.data.local.entity.PostEntity
import com.forma.app.data.local.entity.UserProfileEntity
import com.forma.app.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun observe(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun current(): UserProfileEntity?

    @Upsert
    suspend fun upsert(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET photoUri = :uri")
    suspend fun updatePhoto(uri: String?)

    @Query("DELETE FROM user_profile")
    suspend fun clear()
}

@Dao
interface TrainingDao {
    @Query("SELECT * FROM exercise_log WHERE completed = 1")
    fun observeCompleted(): Flow<List<ExerciseLogEntity>>

    @Upsert
    suspend fun upsertLog(log: ExerciseLogEntity)

    @Query("DELETE FROM exercise_log WHERE dayIndex = :dayIndex")
    suspend fun clearDay(dayIndex: Int)

    @Query("SELECT * FROM workout_session ORDER BY dateEpochMillis DESC")
    fun observeSessions(): Flow<List<WorkoutSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity)
}

@Dao
interface NutritionDao {
    @Query("SELECT * FROM meal_choice")
    fun observeChoices(): Flow<List<MealChoiceEntity>>

    @Query("SELECT * FROM meal_choice WHERE dayIndex = :dayIndex")
    suspend fun choicesForDay(dayIndex: Int): List<MealChoiceEntity>

    @Query("SELECT * FROM meal_choice WHERE dayIndex = :dayIndex AND slotId = :slotId LIMIT 1")
    suspend fun choice(dayIndex: Int, slotId: String): MealChoiceEntity?

    @Upsert
    suspend fun upsert(choice: MealChoiceEntity)
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM post ORDER BY createdAtMillis DESC")
    fun observePosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM author")
    fun observeAuthors(): Flow<List<AuthorEntity>>

    @Query("SELECT * FROM post WHERE id = :id LIMIT 1")
    suspend fun post(id: String): PostEntity?

    @Query("SELECT * FROM author WHERE id = :id LIMIT 1")
    suspend fun author(id: String): AuthorEntity?

    @Query("SELECT COUNT(*) FROM post")
    suspend fun postCount(): Int

    @Upsert
    suspend fun upsertPost(post: PostEntity)

    @Upsert
    suspend fun upsertPosts(posts: List<PostEntity>)

    @Upsert
    suspend fun upsertAuthor(author: AuthorEntity)

    @Upsert
    suspend fun upsertAuthors(authors: List<AuthorEntity>)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_message ORDER BY timestampMillis ASC")
    fun observeMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessageEntity)

    @Query("DELETE FROM chat_message")
    suspend fun clear()
}

@Dao
interface LearnDao {
    @Query("SELECT * FROM article_state")
    fun observeStates(): Flow<List<ArticleStateEntity>>

    @Query("SELECT * FROM article_state WHERE articleId = :id LIMIT 1")
    suspend fun state(id: String): ArticleStateEntity?

    @Upsert
    suspend fun upsert(state: ArticleStateEntity)
}
