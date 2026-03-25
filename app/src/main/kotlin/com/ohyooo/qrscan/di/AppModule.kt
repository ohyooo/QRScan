package com.ohyooo.qrscan.di

import com.ohyooo.qrscan.ScanViewModel
import com.ohyooo.qrscan.util.HistoryRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { HistoryRepository(get()) }
    viewModel { ScanViewModel(get(), get()) }
}
