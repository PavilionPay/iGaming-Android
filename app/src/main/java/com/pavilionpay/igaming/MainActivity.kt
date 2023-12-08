package com.pavilionpay.igaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCenter.start(
            application,
            BuildConfig.APP_CENTER_SECRET,
            Analytics::class.java,
            Crashes::class.java,
        )

        setContent {
            App(
                appModule = SDKDemoApplication.appModule,
            )
        }
    }
}
