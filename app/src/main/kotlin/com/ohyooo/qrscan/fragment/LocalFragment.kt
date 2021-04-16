package com.ohyooo.qrscan.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.mlkit.vision.common.InputImage
import com.ohyooo.qrscan.barcodeClient

class LocalFragment : Fragment(), HasTitle {

    override val title = "local"

    private val vm by activityViewModels<ResultViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = context ?: return null
        return ComposeView(context).apply {
            setContent { SelectButton() }
        }
    }

    @Composable
    fun SelectButton() {
        Button(onClick = { getContent.launch("image/*") }) {
            Text("Select")
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        val image = InputImage.fromFilePath(requireContext(), uri)

        barcodeClient.process(image)
            .addOnSuccessListener { r ->
                r.firstOrNull()?.displayValue?.let {
                    vm.result.value = it
                    vm.currentTab.set(0)
                }
            }
    }

}