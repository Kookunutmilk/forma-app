package com.forma.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.forma.app.data.local.dao.ChatDao
import com.forma.app.data.local.dao.CommunityDao
import com.forma.app.data.local.dao.LearnDao
import com.forma.app.data.local.dao.NutritionDao
import com.forma.app.data.local.dao.ProfileDao
import com.forma.app.data.local.dao.TrainingDao
import com.forma.app.data.local.entity.ArticleStateEntity
import com.forma.app.data.local.entity.AuthorEntity
import com.forma.app.data.local.entity.ChatMessageEntity
import com.forma.app.data.local.entity.ExerciseLogEntity
import com.forma.app.data.local.entity.MealChoiceEntity
import com.forma.app.data.local.entity.PostEntity
import com.forma.app.data.local.entity.UserProfileEntity
import com.forma.app.data.local.entity.WorkoutSessionEntity

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String = value.orEmpty().joinToString("|")

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        value?.split("|")?.filter { it.isNotBlank() }.orEmpty()
}

@Database(
    entities = [
        UserProfileEntity::class,
        ExerciseLogEntity::class,
        WorkoutSessionEntity::class,
        MealChoiceEntity::class,
        AuthorEntity::class,
        PostEntity::class,
        ChatMessageEntity::class,
        ArticleStateEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class FormaDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun trainingDao(): TrainingDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun communityDao(): CommunityDao
    abstract fun chatDao(): ChatDao
    abstract fun learnDao(): LearnDao

    companion object {
        const val NAME = "forma.db"
    }
}
