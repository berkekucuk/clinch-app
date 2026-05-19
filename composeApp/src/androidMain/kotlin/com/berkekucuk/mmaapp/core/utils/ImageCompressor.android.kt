package com.berkekucuk.mmaapp.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

actual fun compressImageByteArray(
    imageBytes: ByteArray,
    maxDimension: Int,
    quality: Int
): ByteArray {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

    val srcWidth = options.outWidth
    val srcHeight = options.outHeight

    if (srcWidth == 0 || srcHeight == 0) return imageBytes

    var inSampleSize = 1
    if (srcHeight > maxDimension || srcWidth > maxDimension) {
        val halfHeight: Int = srcHeight / 2
        val halfWidth: Int = srcWidth / 2
        while (halfHeight / inSampleSize >= maxDimension && halfWidth / inSampleSize >= maxDimension) {
            inSampleSize *= 2
        }
    }

    options.inJustDecodeBounds = false
    options.inSampleSize = inSampleSize
    val decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
        ?: return imageBytes

    val width = decodedBitmap.width
    val height = decodedBitmap.height

    val (newWidth, newHeight) = if (width > height) {
        if (width > maxDimension) {
            maxDimension to (maxDimension * height / width)
        } else width to height
    } else {
        if (height > maxDimension) {
            (maxDimension * width / height) to maxDimension
        } else width to height
    }

    val finalBitmap = if (width != newWidth || height != newHeight) {
        decodedBitmap.scale(newWidth, newHeight)
    } else {
        decodedBitmap
    }

    val outputStream = ByteArrayOutputStream()
    finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

    if (decodedBitmap != finalBitmap) {
        decodedBitmap.recycle()
    }
    finalBitmap.recycle()

    return outputStream.toByteArray()
}