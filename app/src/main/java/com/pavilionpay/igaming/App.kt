package com.pavilionpay.igaming

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
        Scaffold { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background,
            ) {
                var currentScreen by remember {
                    mutableStateOf<NavigationScreens>(
                        NavigationScreens.Landing,
                    )
                }

                Column {
                    Crossfade(
                        targetState = currentScreen,
                        label = currentScreen.route,
                    ) { screen ->
                        when (screen) {
                            NavigationScreens.Landing -> LandingScreen(
                                appModule = appModule,
                                onCurrentScreenChange = { currentScreen = it },
                            )

                            NavigationScreens.PavilionPlaid -> PavilionPlaidScreen(
                                appModule = appModule,
                                onCurrentScreenChange = { currentScreen = it },
                            )
                        }
                    }
                }
            }
        }
    }
}
