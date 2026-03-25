package com.ohyooo.qrscan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ohyooo.qrscan.compose.MainUI
import com.ohyooo.qrscan.compose.theme.QRScanTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanActivity : ComponentActivity() {

    private val vm by viewModel<ScanViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        handleIntent()

        makeStatusBarTransparent()

        setContent {
            QRScanTheme {
                var hasCameraPermission by remember { mutableStateOf(hasCameraPermission()) }
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { granted ->
                    hasCameraPermission = granted
                }

                MainUI(
                    vm = vm,
                    hasCameraPermission = hasCameraPermission,
                    onRequestCameraPermission = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
            }
        }
    }

    private fun makeStatusBarTransparent() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    private fun handleIntent() {
        vm.handleIntent(intent)
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}
