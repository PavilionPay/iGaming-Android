package com.pavilionpay.igaming

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pavilionpay.igaming.di.AppModule
import com.pavilionpay.igaming.presentation.screens.LandingScreen
import com.pavilionpay.igaming.presentation.screens.NavigationScreens
import com.pavilionpay.igaming.presentation.screens.PavilionPlaidScreen
import com.pavilionpay.igaming.ui.theme.IGamingTheme

@Composable
fun App(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    appModule: AppModule,
) {
    IGamingTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
    ) {
        val navController = rememberNavController()
        Scaffold { innerPadding ->
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                NavHost(navController, startDestination = NavigationScreens.Landing.route, Modifier.padding(innerPadding)) {
                    composable(NavigationScreens.Landing.route) {
                        LandingScreen(
                            appModule = appModule,
                            navController = navController,
                        )
                    }
                    composable(
                        route = NavigationScreens.PavilionPlaid.route,
                    ) {
                        PavilionPlaidScreen(
                            appModule = appModule,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }
}
