package com.holden.basicworkouttracker.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ModalView(
    modifier: Modifier = Modifier,
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
        Box(modifier = modifier
            .align(Alignment.Center)
            .fillMaxSize(.7f)
            .clickable(remember { MutableInteractionSource() }, null, onClick = {})
        ) {
            content()
        }
    }
}