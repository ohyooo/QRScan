package com.ohyooo.qrscan.compose

import android.content.Context
import android.util.Log
import android.view.Surface
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ohyooo.qrscan.R
import com.ohyooo.qrscan.util.QrCodeAnalyzer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import java.util.concurrent.Executors

@Composable
fun CameraUI(onQrDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val onCodeDetected by rememberUpdatedState(onQrDetected)
    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    var cameraUnavailable by remember(context, lifecycleOwner) { mutableStateOf(false) }

    LaunchedEffect(context, lifecycleOwner, previewView) {
        try {
            runCamera(context, lifecycleOwner, previewView) { onCodeDetected(it) }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Log.w("QRScanCamera", "Unable to start camera preview", error)
            cameraUnavailable = true
        }
    }

    if (cameraUnavailable) {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.camera_unavailable), color = Color.White)
        }
    } else {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView }
        )
    }
}

private suspend fun runCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    view: PreviewView,
    onQrDetected: (String) -> Unit
) {
    val cameraProvider = ProcessCameraProvider.awaitInstance(context.applicationContext)
    val rotation = view.display?.rotation ?: Surface.ROTATION_0
    val resolutionSelector = ResolutionSelector.Builder()
        .setAspectRatioStrategy(
            AspectRatioStrategy(
                AspectRatio.RATIO_16_9,
                AspectRatioStrategy.FALLBACK_RULE_AUTO
            )
        )
        .build()

    val imageAnalysis = ImageAnalysis.Builder()
        .setResolutionSelector(resolutionSelector)
        .setTargetRotation(rotation)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
    val preview = Preview.Builder()
        .setResolutionSelector(resolutionSelector)
        .setTargetRotation(rotation)
        .build()
        .apply { surfaceProvider = view.surfaceProvider }

    val executor = Executors.newSingleThreadExecutor()
    try {
        imageAnalysis.setAnalyzer(executor, QrCodeAnalyzer(onQrDetected))
        cameraProvider.bindToLifecycle(
            lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
        )
        awaitCancellation()
    } finally {
        try {
            imageAnalysis.clearAnalyzer()
            cameraProvider.unbind(preview, imageAnalysis)
        } finally {
            executor.shutdown()
        }
    }
}
