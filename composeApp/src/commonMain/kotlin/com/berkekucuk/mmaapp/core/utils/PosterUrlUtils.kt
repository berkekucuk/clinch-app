package com.berkekucuk.mmaapp.core.utils

const val CDN_BASE_URL = "https://d3kawnjqpta300.cloudfront.net"
private const val IMAGE_BASE_URL = "https://images.tapology.com"

fun String?.toCdnUrl(): String? {
    if (this.isNullOrBlank()) return null
    if (!startsWith(IMAGE_BASE_URL)) return this
    return CDN_BASE_URL + removePrefix(IMAGE_BASE_URL)
}
