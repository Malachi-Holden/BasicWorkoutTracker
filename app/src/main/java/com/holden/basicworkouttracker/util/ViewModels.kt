package com.holden.basicworkouttracker.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

fun <VM: ViewModel> buildFactory(
    create: () -> VM
): ViewModelProvider.Factory = object:  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return create() as T
    }
}