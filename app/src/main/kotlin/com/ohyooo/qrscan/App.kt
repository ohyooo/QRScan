package com.ohyooo.qrscan

import android.app.Application
import android.content.Context

class App : Application() {

    companion object {
        lateinit var INSTANCE: Context
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = applicationContext
    }
}