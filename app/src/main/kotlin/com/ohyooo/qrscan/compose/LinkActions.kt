package com.ohyooo.qrscan.compose

import android.content.Context
import android.content.Intent
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

internal fun Context.openUri(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
    val activityIntent = Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        startActivity(activityIntent)
    } catch (_: Exception) {
    }
}
