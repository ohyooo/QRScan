package com.ohyooo.qrscan.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.google.mlkit.vision.common.InputImage
import com.ohyooo.qrscan.barcodeClient
import com.ohyooo.qrscan.databinding.FragmentLocalBinding

class LocalFragment : BaseFragment<FragmentLocalBinding>() {

    override val title = "local"

    private val vm by activityViewModels<ResultViewModel>()

    override val vdb by lazy { FragmentLocalBinding.inflate(layoutInflater) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vdb.select.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        val image = InputImage.fromFilePath(requireContext(), uri)

        barcodeClient.process(image)
            .addOnSuccessListener { r ->
                r.firstOrNull()?.displayValue?.let {
                    vm.result.set(it)
                    vm.currentTab.set(0)
                }
            }
    }

}