package com.pavilionpay.igaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.pavilionpay.igaming.presentation.screens.PavilionPlaidViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: PavilionPlaidViewModel by viewModels()
        setContent {
            App(viewModel)
        }
    }
}
