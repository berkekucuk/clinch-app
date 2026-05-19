package com.berkekucuk.mmaapp.core.utils

expect fun compressImageByteArray(
    imageBytes: ByteArray,
    maxDimension: Int = 1024,
    quality: Int = 80
): ByteArray
