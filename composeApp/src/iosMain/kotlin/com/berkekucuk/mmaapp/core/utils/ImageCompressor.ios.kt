package com.berkekucuk.mmaapp.core.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun compressImageByteArray(
    imageBytes: ByteArray,
    maxDimension: Int,
    quality: Int
): ByteArray {
    if (imageBytes.isEmpty()) return imageBytes

    val nsData = imageBytes.usePinned {
        NSData.create(bytes = it.addressOf(0), length = imageBytes.size.toULong())
    }

    val uiImage = UIImage(data = nsData)

    val origWidth = uiImage.size.useContents { width }
    val origHeight = uiImage.size.useContents { height }

    if (origWidth == 0.0 || origHeight == 0.0) return imageBytes

    var newWidth = origWidth
    var newHeight = origHeight

    if (origWidth > origHeight) {
        if (origWidth > maxDimension) {
            newHeight = (maxDimension * origHeight) / origWidth
            newWidth = maxDimension.toDouble()
        }
    } else {
        if (origHeight > maxDimension) {
            newWidth = (maxDimension * origWidth) / origHeight
            newHeight = maxDimension.toDouble()
        }
    }

    UIGraphicsBeginImageContextWithOptions(CGSizeMake(newWidth, newHeight), false, 1.0)
    uiImage.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
    val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    val finalImage = resizedImage ?: uiImage

    val compressionQuality = quality.toDouble() / 100.0
    val compressedData = UIImageJPEGRepresentation(finalImage, compressionQuality)
        ?: return imageBytes

    val compressedBytes = ByteArray(compressedData.length.toInt())
    compressedBytes.usePinned {
        memcpy(it.addressOf(0), compressedData.bytes, compressedData.length)
    }

    return compressedBytes
}