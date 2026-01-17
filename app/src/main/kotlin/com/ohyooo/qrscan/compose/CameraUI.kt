package com.ohyooo.qrscan.compose

import android.content.Context
import android.view.Surface
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ohyooo.qrscan.ScanViewModel
import com.ohyooo.qrscan.util.QrCodeAnalyzer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraUI(vm: ScanViewModel) {
    val lo = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    AndroidView(modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PreviewView(context).also {
                it.post {
                    initCamera(context, lo, vm, it, coroutineScope)
                }
            }
        })
}

//https://www.devbitsandbytes.com/configuring-camerax-in-jetpack-compose-to-take-picture/
private fun initCamera(context: Context, lifecycleOwner: LifecycleOwner, vm: ScanViewModel, view: PreviewView, coroutineScope: CoroutineScope) {
    val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
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

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        var lastTime = 0L
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), QrCodeAnalyzer { r ->
            val now = System.currentTimeMillis()
            if (now - lastTime > 2000) {
                lastTime = now
                vm.result.value = r
            }
        })

        coroutineScope.launch {
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
    }, ContextCompat.getMainExecutor(context))
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, ContextCompat.getMainExecutor(this))
    }
}
