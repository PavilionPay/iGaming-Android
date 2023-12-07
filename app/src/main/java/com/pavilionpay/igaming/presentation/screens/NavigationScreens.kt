package com.pavilionpay.igaming.presentation.screens

sealed class NavigationScreens(val route: String) {
    data object Landing : NavigationScreens("landing")
    data object PavilionPlaid : NavigationScreens("pavilion_plaid")
}
