package com.ohyooo.qrscan.fragment

import androidx.compose.runtime.mutableStateOf
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {
    val result = mutableStateOf("")

    val currentTab = ObservableInt(0)
}