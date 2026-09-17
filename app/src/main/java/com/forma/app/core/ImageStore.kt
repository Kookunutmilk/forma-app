package com.forma.app.core

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

/**
 * Copia las fotos elegidas por el usuario al almacenamiento interno de la app.
 *
 * El selector de fotos del sistema concede permiso temporal sobre el `content://`, así que
 * guardar la ruta tal cual haría que la imagen dejara de verse más adelante.
 */
object ImageStore {

    fun persist(context: Context, uri: Uri): String? = runCatching {
        val directory = File(context.filesDir, "fotos").apply { mkdirs() }
        val target = File(directory, "img_${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        target.absolutePath
    }.getOrNull()
}
