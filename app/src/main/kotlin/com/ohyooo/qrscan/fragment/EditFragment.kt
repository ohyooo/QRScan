package com.ohyooo.qrscan.fragment

import androidx.fragment.app.activityViewModels
import com.ohyooo.qrscan.databinding.FragmentEditBinding

class EditFragment : BaseFragment<FragmentEditBinding>() {

    override val title = "Edit"

    private val vm by activityViewModels<ResultViewModel>()

    override val vdb by lazy(LazyThreadSafetyMode.NONE) { FragmentEditBinding.inflate(layoutInflater).also { it.vm = vm } }
}