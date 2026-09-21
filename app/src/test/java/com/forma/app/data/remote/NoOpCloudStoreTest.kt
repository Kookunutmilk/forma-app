package com.forma.app.data.remote

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoOpCloudStoreTest {

    @Test
    fun disabledAndSafe() = runBlocking {
        val cloud = NoOpCloudStore()
        assertFalse(cloud.isEnabled)
        assertTrue(cloud.pullUserData("uid").exerciseLogs.isEmpty())
        assertTrue(cloud.pullCommunityFeed("uid").posts.isEmpty())
        assertNull(cloud.uploadUserFile("uid", "/tmp/x.jpg", "profile"))
    }
}
