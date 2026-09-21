package com.forma.app

import android.app.Application
import com.forma.app.BuildConfig
import com.forma.app.data.remote.CloudSyncManager
import com.forma.app.data.repository.CommunityRepositoryImpl
import com.forma.app.data.repository.FirebaseAuthRepository
import dagger.Lazy
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

    @Inject
    lateinit var cloudSync: CloudSyncManager

    @Inject
    lateinit var firebaseAuth: Lazy<FirebaseAuthRepository>

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            if (BuildConfig.HAS_FIREBASE) {
                runCatching { firebaseAuth.get().restoreSessionIfNeeded() }
                runCatching { cloudSync.pullIfNeeded(force = true) }
            }
            community.seedIfEmpty()
        }
    }
}
