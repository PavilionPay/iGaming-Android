package com.pavilionpay.igaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.pavilionpay.igaming.presentation.screens.VIPSessionUrlViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: VIPSessionUrlViewModel by viewModels()
        setContent {
            App(viewModel)
        }
    }
}
