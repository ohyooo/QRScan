package com.ohyooo.qrscan

import android.app.Application
import com.ohyooo.qrscan.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QRScanApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@QRScanApp)
            modules(appModule)
        }
    }
}
