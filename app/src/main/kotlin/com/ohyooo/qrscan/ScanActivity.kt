package com.ohyooo.qrscan

import android.content.Intent
import android.os.Bundle
import android.util.Size
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.tabs.TabLayoutMediator
import com.google.mlkit.vision.common.InputImage
import com.ohyooo.qrscan.databinding.ActivityScanBinding
import com.ohyooo.qrscan.fragment.EditFragment
import com.ohyooo.qrscan.fragment.HistoryFragment
import com.ohyooo.qrscan.fragment.LocalFragment
import com.ohyooo.qrscan.fragment.ResultFragment
import com.ohyooo.qrscan.fragment.ResultViewModel
import java.util.concurrent.Executors
import kotlin.math.max

class ScanActivity : AppCompatActivity() {

    private val vm by viewModels<ResultViewModel>()
    private val vdb by lazy(LazyThreadSafetyMode.NONE) { ActivityScanBinding.inflate(layoutInflater) }

    private val fragments = arrayOf(ResultFragment(), EditFragment(), LocalFragment(), HistoryFragment())

    private val behavior by lazy(LazyThreadSafetyMode.NONE) {
        BottomSheetBehavior.from(vdb.bottomSheet).apply {
            halfExpandedRatio = 0.5F
        }
    }

    private var cameraProvider: ProcessCameraProvider? = null

    private val cameraSelector by lazy(LazyThreadSafetyMode.NONE) {
        CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }

    private val preview by lazy(LazyThreadSafetyMode.NONE) {
        Preview.Builder()
            .setTargetResolution(Size(vdb.previewView.width, vdb.previewView.height))
            .build()
            .apply {
                setSurfaceProvider(vdb.previewView.surfaceProvider)
            }
    }

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private val imageAnalysis by lazy(LazyThreadSafetyMode.NONE) {
        ImageAnalysis.Builder()
            .setTargetResolution(Size(vdb.previewView.width, vdb.previewView.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(vdb.root)

        // if (SDK_INT >= Build.VERSION_CODES.R) {
        //     window?.insetsController?.hide(WindowInsets.Type.statusBars())
        // } else {
        //     @Suppress("DEPRECATION")
        //     window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        // }

        initData()
        initViews()

        cameraProvider()

        handleIntent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    private fun handleIntent() {
        if (intent?.type?.startsWith("image/") != true) return
        if (intent?.clipData?.itemCount ?: 0 <= 0) return
        val data = intent?.clipData?.getItemAt(0)?.uri ?: return

        val image = InputImage.fromFilePath(this, data)
        barcodeClient.process(image)
            .addOnCompleteListener { r ->
                if (r.result.isNullOrEmpty()) {
                    Toast.makeText(this, "no data decoded", Toast.LENGTH_SHORT).show()
                } else {
                    r.result?.firstOrNull()?.displayValue?.let {
                        vm.result.set(it)
                        vm.currentTab.set(0)
                    }
                }
            }
    }

    private fun initData() {
        vm.currentTab.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val position = vm.currentTab.get()
                if (vdb.viewPager.currentItem != position) {
                    vdb.viewPager.currentItem = position
                }
            }
        })
    }

    private fun initViews() {
        vdb.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size

            override fun createFragment(position: Int) = fragments[position]
        }

        vdb.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                vm.currentTab.set(position)
            }
        })

        vdb.tabLayout.post {
            val previewHeight = vdb.mainView.height - vdb.previewView.height
            behavior.peekHeight = max(vdb.tabLayout.height, previewHeight) * 2

            (vdb.container.layoutParams as ConstraintLayout.LayoutParams).topMargin = 308.dp
        }

        TabLayoutMediator(vdb.tabLayout, vdb.viewPager) { tab, position ->
            tab.text = fragments[position].title
        }.attach()

        // behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        //     override fun onStateChanged(bottomSheet: View, newState: Int) {
        //         when (newState) {
        //             STATE_HALF_EXPANDED, STATE_EXPANDED -> {
        //                 cameraProvider?.unbindAll()
        //                 vdb.previewView.visibility = View.INVISIBLE
        //             }
        //             STATE_COLLAPSED -> {
        //                 cameraProvider?.bindToLifecycle(this@ScanActivity, cameraSelector, preview, imageAnalysis)
        //                 vdb.previewView.visibility = View.VISIBLE
        //             }
        //         }
        //     }
        //
        //     override fun onSlide(bottomSheet: View, slideOffset: Float) {
        //     }
        // })
    }

    private fun cameraProvider() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            startCamera(cameraProvider!!)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startCamera(cameraProvider: ProcessCameraProvider) {
        enableScan()

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
    }

    private var lastTime = 0L

    private fun enableScan() {
        imageAnalysis.setAnalyzer(cameraExecutor, QrCodeAnalyzer { r ->
            val now = System.currentTimeMillis()
            if (now - lastTime > 1000) {
                lastTime = now
                vm.result.set(r)
            }
        })
    }

    private fun disableScan() {
        imageAnalysis.clearAnalyzer()
    }

    private fun expandHalfSheet() {
        if (behavior.state != STATE_HALF_EXPANDED || behavior.state != STATE_EXPANDED) {
            behavior.state = STATE_HALF_EXPANDED
        }
    }

    // dp to px
    val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}