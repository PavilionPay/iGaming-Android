package com.pavilionpay.igaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pavilionpay.igaming.presentation.screens.CashierScreen
import com.pavilionpay.igaming.presentation.screens.EditUserScreen
import com.pavilionpay.igaming.presentation.screens.LandingScreen
import com.pavilionpay.igaming.presentation.screens.PavilionPlaidScreen
import com.pavilionpay.igaming.presentation.screens.VIPSessionUrlViewModel
import com.pavilionpay.igaming.ui.theme.IGamingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: VIPSessionUrlViewModel by viewModels()
        setContent {
            IGamingTheme {
                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = NavigationScreens.Landing.name,
                            modifier = Modifier.fillMaxSize(),
                            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400)) },
                            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400)) },
                            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400)) },
                            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400)) },
                            builder = fun NavGraphBuilder.() {
                                composable(NavigationScreens.Landing.name) {
                                    LandingScreen(viewModel, {
                                        navController.navigate(NavigationScreens.EditUser.name)
                                    }, {
                                        navController.navigate(NavigationScreens.PavilionPlaid.name)
                                    }, {
                                        navController.navigate(NavigationScreens.CashierView.name)
                                    })
                                }
                                composable(NavigationScreens.PavilionPlaid.name) {
                                    PavilionPlaidScreen(viewModel) { navController.popBackStack() }
                                }
                                composable(NavigationScreens.EditUser.name) {
                                    EditUserScreen(viewModel) { navController.popBackStack() }
                                }
                                composable(NavigationScreens.CashierView.name) {
                                    CashierScreen(viewModel) { navController.popBackStack() }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    enum class NavigationScreens {
        Landing,
        PavilionPlaid,
        CashierView,
        EditUser
    }
}


