package com.forma.app.di

import android.content.Context
import androidx.room.Room
import com.forma.app.BuildConfig
import com.forma.app.data.ai.LocalAiRepository
import com.forma.app.data.local.FormaDatabase
import com.forma.app.data.local.FormaPreferences
import com.forma.app.data.local.dao.ChatDao
import com.forma.app.data.local.dao.CommunityDao
import com.forma.app.data.local.dao.LearnDao
import com.forma.app.data.local.dao.NutritionDao
import com.forma.app.data.local.dao.ProfileDao
import com.forma.app.data.local.dao.TrainingDao
import com.forma.app.data.repository.ChatRepositoryImpl
import com.forma.app.data.repository.CommunityRepositoryImpl
import com.forma.app.data.repository.FirebaseAuthRepository
import com.forma.app.data.repository.LearnRepositoryImpl
import com.forma.app.data.repository.LocalAuthRepository
import com.forma.app.data.repository.NutritionRepositoryImpl
import com.forma.app.data.repository.ProfileRepositoryImpl
import com.forma.app.data.repository.TrainingRepositoryImpl
import com.forma.app.domain.repository.AiRepository
import com.forma.app.domain.repository.AuthRepository
import com.forma.app.domain.repository.ChatRepository
import com.forma.app.domain.repository.CommunityRepository
import com.forma.app.domain.repository.LearnRepository
import com.forma.app.domain.repository.NutritionRepository
import com.forma.app.domain.repository.ProfileRepository
import com.forma.app.domain.repository.TrainingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FormaDatabase =
        Room.databaseBuilder(context, FormaDatabase::class.java, FormaDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProfileDao(db: FormaDatabase): ProfileDao = db.profileDao()

    @Provides
    fun provideTrainingDao(db: FormaDatabase): TrainingDao = db.trainingDao()

    @Provides
    fun provideNutritionDao(db: FormaDatabase): NutritionDao = db.nutritionDao()

    @Provides
    fun provideCommunityDao(db: FormaDatabase): CommunityDao = db.communityDao()

    @Provides
    fun provideChatDao(db: FormaDatabase): ChatDao = db.chatDao()

    @Provides
    fun provideLearnDao(db: FormaDatabase): LearnDao = db.learnDao()

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context): FormaPreferences =
        FormaPreferences(context)
}

/**
 * Selección del motor de IA. Hoy la app usa [LocalAiRepository], que es determinista y funciona
 * sin credenciales. Para conectar un proveedor real basta con cambiar este binding por
 * `RemoteAiRepository` y definir `FORMA_AI_API_KEY` en `local.properties`.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: LocalAiRepository): AiRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        local: dagger.Lazy<LocalAuthRepository>,
        firebase: dagger.Lazy<FirebaseAuthRepository>,
    ): AuthRepository =
        if (BuildConfig.HAS_FIREBASE) firebase.get() else local.get()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindTrainingRepository(impl: TrainingRepositoryImpl): TrainingRepository

    @Binds
    @Singleton
    abstract fun bindNutritionRepository(impl: NutritionRepositoryImpl): NutritionRepository

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(impl: CommunityRepositoryImpl): CommunityRepository

    @Binds
    @Singleton
    abstract fun bindLearnRepository(impl: LearnRepositoryImpl): LearnRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
