package com.example.sealnote.di


import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
object ImageSaver {
    fun copyTempToGallery(context: Context, tempFile: File): Uri? {
        val resolver = context.contentResolver

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, tempFile.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SealNote")
            if (Build.VERSION.SDK_INT >= 29) put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val galleryUri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        ) ?: return null

        resolver.openOutputStream(galleryUri).use { outStream ->
            FileInputStream(tempFile).use { inStream ->
                inStream.copyTo(outStream as OutputStream)
            }
        }

        if (Build.VERSION.SDK_INT >= 29) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(galleryUri, values, null, null)
        }

        return galleryUri
    }
}