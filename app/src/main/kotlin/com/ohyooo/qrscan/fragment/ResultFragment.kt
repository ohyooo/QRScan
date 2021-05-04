package com.ohyooo.qrscan.fragment

import androidx.fragment.app.activityViewModels
import com.ohyooo.qrscan.databinding.FragmentResultBinding

class ResultFragment : BaseFragment<FragmentResultBinding>() {

    override val title = "Result"

    private val vm by activityViewModels<ResultViewModel>()

    override val vdb by lazy(LazyThreadSafetyMode.NONE) { FragmentResultBinding.inflate(layoutInflater, container, false).also { it.vm = vm } }
}