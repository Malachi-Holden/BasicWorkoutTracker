package com.holden.basicworkouttracker.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ModalView(
    visible: Boolean,
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {
    if (!visible) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = .5f))
            .clickable {
                onClose()
            }
    ) {
        Box(modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize(.7f)) {
            content()
        }
    }
}