package com.ohyooo.qrscan.util

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(private val qrCallback: (qrCode: String) -> Unit) : ImageAnalysis.Analyzer {

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        if (imageProxy.image == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeClient.process(image)
            .addOnSuccessListener { r ->
                r.firstOrNull()?.displayValue?.let { qrCallback(it) }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

val barcodeClient by lazy(LazyThreadSafetyMode.NONE) {
    BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
    )
}
