package com.pavilionpay.vipconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
                                LandingView(viewModel) { sessionUrl ->
                                    CustomTabsIntent.Builder().build().apply {
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    }.launchUrl(this@MainActivity, Uri.parse(sessionUrl))
                                }


                            }

                            // SDK WebView Screen
                            composable(NavigationScreens.VipSdk.name) {
                                VipConnectScreen(
                                    launchUrl = sessionUrl,
                                    { navController.popBackStack() }
                                )
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
}


