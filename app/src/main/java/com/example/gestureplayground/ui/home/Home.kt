package com.example.gestureplayground.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.gestureplayground.ui.home.model.Point
import com.example.gestureplayground.ui.home.model.Region
import kotlin.math.roundToInt

const val TAG = "GesturePlayground"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        Modifier,
    ) { innerPadding ->
        println(innerPadding)
        TransformableSample()
    }
}

@Composable
fun TransformableSample() {

    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        Modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    scale = maxOf(0.4f, scale * zoom)
//                    rotation += rotation //Probably shouldn't rotate
                    offset += pan
                }
            }
            .fillMaxSize(),
    ) {

        val x = 32
        val y = 32

        var canvasSize by remember { mutableStateOf(Size.Zero) }

        var isPressed by remember { mutableStateOf(false) }
        val helperSize: Dp by animateDpAsState(if (isPressed) 24.dp else 0.dp)

        var currPointOffset by remember {
            mutableStateOf(Offset.Zero)
        }
        val points = remember { mutableStateListOf<Point>() }

        DrawingCanvas(
            x = x, y = y,
            gridScale = 4,
            debug = true,
            modifier = Modifier
                .align(Alignment.Center)
                .drawBehind { canvasSize = this.size }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation,
                    translationX = offset.x,
                    translationY = offset.y,
                )
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { origin ->
                            isPressed = true
                            currPointOffset = origin
                        },
                        onDrag = { _, dragAmount ->
                            currPointOffset += dragAmount
                        },
                        onDragEnd = {
                            points.add(Point(currPointOffset))
                            isPressed = false
                        },
                    )
                },
        ) {
            val canvasWidth = canvasSize.width
            val canvasHeight = canvasSize.height
            val cellWidth = (canvasWidth / x)
            val cellHeight = (canvasHeight / y)

            //Controller dot used to display interaction with canvas
            Box(
                Modifier
                    .offset(-helperSize / (2 * scale), -helperSize / (2 * scale))
                    .offset {
                        IntOffset(
                            getSnapTarget(
                                currPointOffset.x, cellWidth
                            ).coerceIn(0..canvasWidth.roundToInt()),
                            getSnapTarget(
                                currPointOffset.y, cellHeight
                            ).coerceIn(0..canvasHeight.roundToInt()),
                        )
                    }
                    .size(helperSize / scale)
                    .clip(CircleShape)
                    .background(Color.Red),
            )


            for (i in 0..points.lastIndex) {
                DraggableDot(
                    points[i],
                    scale,
                    cellWidth,
                    Region(0..canvasWidth.roundToInt(), 0..canvasHeight.roundToInt()),
                    onDragUpdate = { currPointOffset = it },
                    onRelease = { points[i] = it },
                    modifier = Modifier
                )
            }

            Text(
                "(${currPointOffset.getScaledOffset(cellWidth)})",
                textAlign = TextAlign.Center,
                modifier = Modifier.background(Color.Green)
            )
        }

        Text(
            "(${currPointOffset})",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(Color.Red)
        )
    }
}

fun getSnapTarget(coordinateValue: Float, cellSize: Float) =
    (getScaledCoordinate(coordinateValue, cellSize) * cellSize).roundToInt()

fun getScaledCoordinate(coordinateValue: Float, cellSize: Float): Int {
    if (coordinateValue == 0f) return 0
    return (coordinateValue / cellSize).roundToInt()
}

fun Offset.getScaledOffset(cellSize: Float): IntOffset {
    return IntOffset(
        getScaledCoordinate(x, cellSize), getScaledCoordinate(y, cellSize)
    )
}