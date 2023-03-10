package com.example.gestureplayground.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.gestureplayground.ui.home.model.Point
import com.example.gestureplayground.ui.home.model.Region

@Composable
fun DraggableDot(
    point: Point,
    scale: Float,
    cellSize: Float,
    boundary: Region,
    modifier: Modifier = Modifier,
    onDragUpdate: (Offset) -> Unit = {},
    onRelease: (Point) -> Unit,
) {

    var pointOffset by remember {
        mutableStateOf(point.coordinates)
    }
    var isPressed by remember { mutableStateOf(false) }
    val size: Dp by animateDpAsState(if (isPressed) 24.dp else 12.dp)

    Box(
        modifier
            .offset(-size / (2 * scale), -size / (2 * scale))
            .offset {
                IntOffset(
                    getSnapTarget(
                        pointOffset.x, cellSize
                    ).coerceIn(boundary.x),
                    getSnapTarget(
                        pointOffset.y, cellSize
                    ).coerceIn(boundary.y),
                )
            }
            .size(size / scale)
            .clip(CircleShape)
            .background(Color.Black)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { origin ->
                        isPressed = true
                    },
                    onDrag = { change, dragAmount ->
                        pointOffset += dragAmount
                        onDragUpdate(pointOffset)
                    },
                    onDragEnd = {
                        onRelease(point.copy(pointOffset))
                        isPressed = false
                    },
                )
            },
    )
}