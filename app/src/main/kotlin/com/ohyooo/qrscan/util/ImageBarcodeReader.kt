package com.ohyooo.qrscan.util

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ImageBarcodeReader(private val context: Context) {

    suspend fun readFirstDisplayValue(uri: Uri): String? {
        return barcodeClient.process(InputImage.fromFilePath(context, uri))
            .await()
            .firstNotNullOfOrNull { barcode ->
                barcode.displayValue?.trim()?.takeIf { it.isNotBlank() }
            }
    }
}

private suspend fun Task<List<Barcode>>.await(): List<Barcode> =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            if (continuation.isActive) {
                continuation.resume(result)
            }
        }
        addOnFailureListener { throwable ->
            if (continuation.isActive) {
                continuation.resumeWithException(throwable)
            }
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
