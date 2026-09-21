package com.forma.app.di

import com.forma.app.BuildConfig
import com.forma.app.data.remote.FirebaseCloudStore
import com.forma.app.data.remote.FormaCloudStore
import com.forma.app.data.remote.NoOpCloudStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudModule {

    @Provides
    @Singleton
    fun provideCloudStore(): FormaCloudStore =
        if (BuildConfig.HAS_FIREBASE) FirebaseCloudStore() else NoOpCloudStore()
}
