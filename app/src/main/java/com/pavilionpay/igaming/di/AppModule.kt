package com.pavilionpay.igaming.di

import com.pavilionpay.igaming.presentation.screens.PavilionPlaidViewModel
import com.pavilionpay.igaming.remote.PavilionService
import com.pavilionpay.igaming.remote.TokenGenerator

interface AppModule {
    val pavilionService: PavilionService
    val pavilionPlaidViewModel: PavilionPlaidViewModel
}

class AppModuleImpl : AppModule {
    override val pavilionService: PavilionService by lazy {
        PavilionService.create(tokenGenerator.generate())
    }

    override val pavilionPlaidViewModel: PavilionPlaidViewModel by lazy {
        PavilionPlaidViewModel(
            pavilionService = pavilionService,
        )
    }

    private val tokenGenerator: TokenGenerator by lazy {
        TokenGenerator()
    }
}
