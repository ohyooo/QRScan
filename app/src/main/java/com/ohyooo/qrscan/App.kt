package com.ohyooo.qrscan

import android.app.Application
import android.content.Context
import com.orhanobut.hawk.Hawk

class App : Application() {

    companion object {
        lateinit var INSTANCE: Context
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = applicationContext

        Hawk.init(this).build()
    }
}