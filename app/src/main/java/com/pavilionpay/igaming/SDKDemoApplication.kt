package com.pavilionpay.igaming

import android.app.Application
import com.pavilionpay.igaming.di.AppModule
import com.pavilionpay.igaming.di.AppModuleImpl

class SDKDemoApplication : Application() {
    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl()
    }
}
