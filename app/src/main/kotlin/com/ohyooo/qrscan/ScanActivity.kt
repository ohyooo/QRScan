package com.ohyooo.qrscan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
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

        syncCameraPermission()
        handleIntent(intent)

        makeStatusBarTransparent()

        setContent {
            QRScanTheme {
                MainUI(vm = vm)
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
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        syncCameraPermission()
    }

    private fun handleIntent(intent: Intent?) {
        intent?.sharedImageUri()?.let { uri ->
            vm.dispatch(ScanIntent.ExternalImageReceived(uri))
        }
    }

    private fun syncCameraPermission() {
        vm.dispatch(ScanIntent.CameraPermissionChanged(hasCameraPermission()))
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun Intent.sharedImageUri(): Uri? {
        if (type?.startsWith("image/") != true) return null

        return clipData?.getItemAt(0)?.uri
            ?: IntentCompat.getParcelableExtra(this, Intent.EXTRA_STREAM, Uri::class.java)
    }
}
