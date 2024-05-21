package com.pavilionpay.igaming.presentation.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pavilionpay.igamingkit.PavilionPlaidWebView

@Composable
fun CashierScreen(
        viewModel: VIPSessionUrlViewModel,
        navigateUp: () -> Unit,
) {
    val patronSessionUrls by viewModel.patronSessionUrlState.collectAsStateWithLifecycle()
    CashierView(
        patronSessionUrl = patronSessionUrls.patronSessionUrl,
        redirectUrl = patronSessionUrls.patronSessionRedirectUrl,
        onFullScreenRequested = {
            viewModel.clearPatronSession()
            viewModel.isFullScreenRequested = true
            navigateUp()
        },
        navigateUp
    )
}

@Composable
fun CashierView(
        patronSessionUrl: String,
        redirectUrl: String,
        onFullScreenRequested: () -> Unit,
        navigateUp: () -> Unit,
) {
    if (patronSessionUrl.isNotBlank()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text("Cashier Page View", fontSize = 24.sp, modifier = Modifier.padding(top = 50.dp))
                Text(
                    "Example of how the SDK would look in the condensed Cashier Page format",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                            .padding(top = 50.dp)
                            .padding(horizontal = 50.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                            .fillMaxHeight(0.7f)
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .padding(bottom = 20.dp)
                            .border(1.dp, Color.Black)
                ) {
                    if (!LocalInspectionMode.current) {
                        PavilionPlaidWebView(
                            url = patronSessionUrl,
                            redirectUrl = redirectUrl,
                            onFullScreenRequested = onFullScreenRequested,
                            onClose = {
                                navigateUp()
                            },
                        )
                    }
                }
            }
        }
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

@Preview
@Composable
fun CashierViewPreview() {
    CashierView("Something", "", {}) {}
}