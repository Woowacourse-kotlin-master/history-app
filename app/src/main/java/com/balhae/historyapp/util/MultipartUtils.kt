package com.balhae.historyapp.util

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object MultipartUtils {

    fun createImagePartFromUri(context: Context, uri: Uri, partName: String = "image"): MultipartBody.Part? {
        val contentResolver = context.contentResolver ?: return null
        val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null

        val tempFile = File.createTempFile("heritage_", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()

        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), tempFile)
        return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
    }
}
