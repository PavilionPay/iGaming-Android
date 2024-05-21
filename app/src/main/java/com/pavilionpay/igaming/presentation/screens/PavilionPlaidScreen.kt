package com.pavilionpay.igaming.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pavilionpay.igamingkit.PavilionPlaidWebView

@Composable
fun PavilionPlaidScreen(
        viewModel: VIPSessionUrlViewModel,
        navigateUp: () -> Unit,
) {
    val patronSessionUrls by viewModel.patronSessionUrlState.collectAsStateWithLifecycle()

    if (patronSessionUrls.patronSessionUrl.isNotBlank()) {
        PavilionPlaidWebView(
            url = patronSessionUrls.patronSessionUrl,
            redirectUrl = patronSessionUrls.patronSessionRedirectUrl,
            onFullScreenRequested = {},
            onClose = {
                viewModel.clearPatronSession()
                navigateUp()
            },
        )
    } else {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(Modifier.size(40.dp))
            }
        }
    }
}
