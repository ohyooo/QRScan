package com.ohyooo.qrscan.compose

import android.content.Context
import android.content.Intent
import android.net.Uri

internal fun Context.openUri(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
    val activityIntent = Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        startActivity(activityIntent)
    } catch (_: Exception) {
    }
}
