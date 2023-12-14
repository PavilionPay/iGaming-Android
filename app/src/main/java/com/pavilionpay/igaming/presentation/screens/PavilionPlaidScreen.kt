package com.pavilionpay.igaming.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pavilionpay.igaming.di.AppModule
import com.pavilionpay.igaming.presentation.viewModelFactory
import com.pavilionpay.igamingkit.PavilionPlaidWebView

@Composable
fun PavilionPlaidScreen(
    appModule: AppModule,
    onCurrentScreenChange: (NavigationScreens) -> Unit,
) {
    val viewModel: PavilionPlaidViewModel = viewModel(
        factory = viewModelFactory { appModule.pavilionPlaidViewModel },
    )

    val patronSessionUrls by viewModel.patronSessionUrlState.collectAsStateWithLifecycle()

    if (patronSessionUrls.patronSessionUrl.isNotBlank()) {
        PavilionPlaidWebView(
            url = patronSessionUrls.patronSessionUrl,
            redirectUrl = patronSessionUrls.patronSessionRedirectUrl,
            onClose = {
                viewModel.clearPatronSession()
                onCurrentScreenChange(NavigationScreens.Landing)
            },
        )
    }
}
