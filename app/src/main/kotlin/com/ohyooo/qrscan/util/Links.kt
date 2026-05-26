package com.ohyooo.qrscan.util

import android.net.Uri
import androidx.core.net.toUri

internal fun String.toOpenableUri(): Uri? {
    val trimmed = trim()
    if (trimmed.isBlank() || trimmed.contains('\n')) return null

    val directUri = trimmed.toUri()
    if (!directUri.scheme.isNullOrBlank()) {
        return directUri
    }

    val looksLikeDomain = '.' in trimmed && ' ' !in trimmed
    if (!looksLikeDomain) return null

    return "https://$trimmed".toUri()
}
