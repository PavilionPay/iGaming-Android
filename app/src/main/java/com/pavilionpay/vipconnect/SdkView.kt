package com.pavilionpay.vipconnect

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipConnectScreen(
    launchUrl: String,
    navigateUp: () -> Unit,
) {

    Scaffold() { padding ->
        val context = LocalContext.current

        Surface(Modifier.padding(padding)) {

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            WebView.setWebContentsDebuggingEnabled(true)
                            @SuppressLint("SetJavaScriptEnabled")
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true

                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    if (request?.url.toString().contains(VIPSessionUrlViewModel.RETURN_URL)) {
                                        navigateUp()
                                        return true
                                    }

                                    return false
                                }
                            }

                            loadUrl(launchUrl)
                        }
                    },
                )
            }

        }
    }
}