package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

/**
 * Handles converting user-uploaded image files / Uris to exact base64 data URIs
 * ("data:image/jpeg;base64,...") and decoding them back for instant direct display.
 */
object ImageBase64Helper {
    private const val TAG = "ImageBase64Helper"

    /**
     * Converts a content Uri to an exact base64 data URI string.
     * Scales image so it stays within a safe, fast payload size for Firestore.
     */
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (originalBitmap == null) return null

            val maxDimension = 1200
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = if (width > maxDimension || height > maxDimension) {
                maxDimension.toFloat() / maxOf(width, height)
            } else 1.0f

            val scaledBitmap = if (scale < 1.0f) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    (width * scale).toInt().coerceAtLeast(1),
                    (height * scale).toInt().coerceAtLeast(1),
                    true
                )
            } else originalBitmap

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val bytes = outputStream.toByteArray()
            val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64String"
        } catch (e: Exception) {
            Log.e(TAG, "uriToBase64 error: ${e.message}", e)
            null
        }
    }

    /**
     * Extracts byte array from base64 string or data URI for instant, direct rendering.
     */
    fun decodeBase64ToByteArray(base64OrDataUri: String): ByteArray? {
        return try {
            val cleanBase64 = if (base64OrDataUri.contains(",")) {
                base64OrDataUri.substringAfter(",")
            } else {
                base64OrDataUri
            }
            Base64.decode(cleanBase64, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    fun isBase64String(str: String?): Boolean {
        if (str.isNullOrBlank()) return false
        return str.startsWith("data:image/") ||
                (str.length > 100 && !str.startsWith("http://") && !str.startsWith("https://") && !str.startsWith("content://") && !str.startsWith("file://"))
    }
}
