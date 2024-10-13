package com.holden.basicworkouttracker.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T, S> StateFlow<T>.mapAsComposeState(transform: @Composable (T) -> S): S {
    return transform(collectAsState().value)
}

enum class Side {
    Start, Top, End, Bottom
}

fun Modifier.singleEdge(
    color: Color,
    thickness: Dp,
    side: Side
) = drawBehind {
    val (lineStart, lineEnd) = when (side) {
        Side.Start -> Offset(0f, 0f) to Offset(0f, size.height)
        Side.Top -> Offset(0f, 0f) to Offset(size.width, 0f)
        Side.End -> Offset(size.width, 0f) to Offset(size.width, size.height)
        Side.Bottom -> Offset(0f, size.height) to Offset(size.width, size.height)
    }
    drawLine(
        color,
        lineStart,
        lineEnd,
        thickness.value * density
    )
}