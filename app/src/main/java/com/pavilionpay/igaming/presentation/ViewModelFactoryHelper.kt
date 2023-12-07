package com.pavilionpay.igaming.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <VM : ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val vm = initializer()
            if (modelClass.isAssignableFrom(vm::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return vm as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
