package com.ohyooo.qrscan.di

import com.ohyooo.qrscan.ScanViewModel
import com.ohyooo.qrscan.util.HistoryRepository
import com.ohyooo.qrscan.util.ImageBarcodeReader
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { HistoryRepository(get()) }
    single { ImageBarcodeReader(get()) }
    viewModel { ScanViewModel(get(), get()) }
}
