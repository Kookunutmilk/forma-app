package com.forma.app

import android.app.Application
import com.forma.app.data.repository.CommunityRepositoryImpl
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FormaApplication : Application() {

    @Inject
    lateinit var community: CommunityRepositoryImpl

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        scope.launch { community.seedIfEmpty() }
    }
}
