package com.ohyooo.qrscan.compose

import android.content.Context
import android.view.Surface
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ohyooo.qrscan.util.QrCodeAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraUI(onQrDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }

    LaunchedEffect(context, lifecycleOwner, previewView, onQrDetected) {
        initCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            view = previewView,
            executor = executor,
            onQrDetected = onQrDetected
        )
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { previewView }
    )
}

private suspend fun initCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    view: PreviewView,
    executor: ExecutorService,
    onQrDetected: (String) -> Unit
) {
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()
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
        .apply {
            setAnalyzer(executor, QrCodeAnalyzer(onQrDetected))
        }

    val cameraProvider = context.getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(rotation)
            .build()
            .apply {
                surfaceProvider = view.surfaceProvider
            },
        imageAnalysis
    )
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, ContextCompat.getMainExecutor(this))
    }
}
