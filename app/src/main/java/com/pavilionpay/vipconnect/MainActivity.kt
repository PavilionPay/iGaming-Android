package com.pavilionpay.vipconnect

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

/**
 * A simple Activity that shows a Landing view, and a fullscreen WebView hosing the VIP SDK.
 * This app uses Compose to create its UI, but Compose is not a requirement for the VIP SDK.
 * As long as you setup your WebView correctly, the SDK will load correctly inside it.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: VIPSessionUrlViewModel by viewModels()
        setContent {
            Scaffold { innerPadding ->
                Surface(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    var sessionUrl by remember { mutableStateOf("") }
                    var showAlert by remember { mutableStateOf(false) }

                    NavHost(
                        navController = navController,
                        startDestination = NavigationScreens.Landing.name,
                        modifier = Modifier.fillMaxSize(),
                        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400)) },
                        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400)) },
                        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400)) },
                        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400)) },
                        builder = fun NavGraphBuilder.() {

                            // Landing screen view
                            // When the Launch button is pressed, the VIPSessionUrlViewModel attempts to retrieve a valid session id,
                            // and if successful will build the VIP SDK url for that session and navigate to the VIP SDK WebView screen
                            // to launch that url.
                            composable(NavigationScreens.Landing.name) {
                                LandingView(viewModel) { sessionId ->
                                    if (sessionId?.isNotEmpty() == true) {
                                        sessionUrl = viewModel.createVIPSessionUrl(sessionId)
                                        navController.navigate(NavigationScreens.VipSdk.name)
                                    } else {
                                        showAlert = true
                                    }
                                }

                                if (showAlert) {
                                    AlertDialog(onDismissRequest = { showAlert = false },
                                        confirmButton = {
                                            TextButton(onClick = { showAlert = false }) {
                                                Text("OK")
                                            }
                                        },
                                        title = {
                                            Text("Session Creation Failed")
                                        },
                                        text = {
                                            Text("Could not create a valid session; please check your secret values in VIPSessionUrlViewModel.")
                                        }
                                    )
                                }
                            }

                            // SDK WebView Screen
                            composable(NavigationScreens.VipSdk.name) {
                                VipSdkView(sessionUrl) { navController.popBackStack() }
                            }
                        }
                    )
                }
            }
        }
    }

    enum class NavigationScreens {
        Landing,
        VipSdk
    }

    @Composable
    fun VipSdkView(sessionUrl: String, navigateUp: () -> Unit) {
        val context = LocalContext.current

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                // These steps are required to setup a VIP SDK WebView:
                // 1. Create a WebViewClient that implements shouldOverrideUrlLoading and looks for navigation
                //      to the value passed as `returnURL` during session creation. When that url is reached,
                //      dismiss the WebView or navigate up.
                // 2. Enabled JavaScript and DOM storage on the WebView, as the VIP SDK uses both of these features.
                // 3. Load the VIP SDK launch url with a valid session id
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            if (request?.url.toString().contains("closevip")) {
                                navigateUp()
                                return true
                            }

                            return false
                        }
                    }

                    @SuppressLint("SetJavaScriptEnabled")
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    loadUrl(sessionUrl)
                }
            },
        )
    }

    @Composable
    fun LandingView(viewModel: VIPSessionUrlViewModel, setSessionId: (String?) -> Unit) {
        val scope = rememberCoroutineScope()
        var loading by remember { mutableStateOf(false) }

        Surface {
            Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 150.dp)
                        .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(text = "VIP Connect Reference App", fontSize = 28.sp)
                Spacer(modifier = Modifier.fillMaxHeight(.85f))

                Button(
                    onClick = {
                        scope.launch {
                            loading = true
                            setSessionId(viewModel.getPatronSessionId())
                            loading = false
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                            .wrapContentWidth()
                            .height(56.dp),
                ) {
                    Text("Launch VIP SDK")
                }
            }

            if (loading) {
                Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x65000000)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(Modifier.size(80.dp))
                }
            }
        }

    }
}


