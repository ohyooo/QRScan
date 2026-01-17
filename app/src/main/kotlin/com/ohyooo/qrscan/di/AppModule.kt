package com.ohyooo.qrscan.di

import com.ohyooo.qrscan.ScanViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { ScanViewModel(get()) }
}
