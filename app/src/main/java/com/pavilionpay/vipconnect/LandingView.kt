package com.pavilionpay.vipconnect

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingView(
    viewModel: VIPSessionUrlViewModel,
    setSessionUrl: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    var sessionId by remember { mutableStateOf<String?>("") }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 150.dp)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "VIP Connect\nReference App",
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.fillMaxHeight(.85f))

            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        sessionId = viewModel.getPatronSessionId()
                        if (sessionId != null) {
                            setSessionUrl(viewModel.createVIPSessionUrl(sessionId!!))
                        } else {
                            showAlert = true
                        }
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

}