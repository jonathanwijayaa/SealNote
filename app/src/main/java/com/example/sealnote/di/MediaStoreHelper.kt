package com.example.sealnote.di

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

object MediaStoreHelper {

    /** Buat entry baru **dengan IS_PENDING = 1**  */
    fun createImageUri(context: Context): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE,  "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SealNote")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
    }

    /** Panggil setelah foto BERHASIL di‑capture */
    fun publishImage(context: Context, uri: Uri) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        context.contentResolver.update(uri, values, null, null)
    }
}