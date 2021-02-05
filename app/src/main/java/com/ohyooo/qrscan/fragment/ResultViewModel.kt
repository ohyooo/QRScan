package com.ohyooo.qrscan.fragment

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {
    val result = ObservableField("")

    val currentTab = ObservableInt(0)
}