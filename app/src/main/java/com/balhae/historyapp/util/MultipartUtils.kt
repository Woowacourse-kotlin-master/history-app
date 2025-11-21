package com.balhae.historyapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

        try {
            // 이미지 압축
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) {
                return null
            }

            // 이미지 크기 조정 (최대 1200x1200)
            val scaledBitmap = scaleImage(bitmap, 1200, 1200)

            // 압축된 이미지를 파일로 저장
            val tempFile = File.createTempFile("heritage_", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { output ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
            }

            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), tempFile)
            return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 이미지 크기를 조정 (비율 유지)
     */
    private fun scaleImage(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val scale = minOf(
            maxWidth.toFloat() / width,
            maxHeight.toFloat() / height
        )

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
