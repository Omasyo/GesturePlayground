package com.example.gestureplayground.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration

@Composable
fun DrawingCanvas(
    x: Int, y: Int,
    modifier: Modifier = Modifier,
    gridScale: Int = 0,
    showGrid: Boolean = true,
    gridColor: Color = Color.Black,
    debug: Boolean = false,
    debugGridColor: Color = Color.Green,
    content: @Composable (BoxScope.() -> Unit) = {}
) {
    fun ViewConfiguration.withoutTouchSlop() = object : ViewConfiguration {
        override val doubleTapMinTimeMillis: Long
            get() = this@withoutTouchSlop.doubleTapMinTimeMillis
        override val doubleTapTimeoutMillis: Long
            get() = this@withoutTouchSlop.doubleTapTimeoutMillis
        override val longPressTimeoutMillis: Long
            get() = this@withoutTouchSlop.longPressTimeoutMillis
        override val touchSlop: Float
            get() = 0f
    }

    CompositionLocalProvider(LocalViewConfiguration provides LocalViewConfiguration.current.withoutTouchSlop()) {
        Box(
            modifier = modifier
                .aspectRatio(x.toFloat() / y)
//            .fillMaxSize()
//            .background(Color.White)
                .drawBehind {

                    val test = Path()
                    test.cubicTo(10f, 10f, 20f, 20f, 30f, 30f)
                    drawPath(test, Color.Magenta)

                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val cellWidth = (canvasWidth / x)
                    val cellHeight = (canvasHeight / y)

                    Log.d(TAG, "DrawingCanvas: Drawing width is $cellWidth")

                    //debugGrid
                    if (debug) {
                        for (i in 0..x) {
                            drawLine(
                                start = Offset(x = i * cellWidth, y = 0f),
                                end = Offset(x = i * cellWidth, y = canvasHeight),
                                color = debugGridColor
                            )
                        }
                        for (i in 0..y) {
                            drawLine(
                                start = Offset(x = 0f, y = i * cellHeight),
                                end = Offset(x = canvasWidth, y = i * cellHeight),
                                color = debugGridColor
                            )
                        }
                    }

                    if (showGrid) {
                        for (i in 0..x / gridScale) {
                            drawLine(
                                start = Offset(x = i * cellWidth * gridScale, y = 0f),
                                end = Offset(x = i * cellWidth * gridScale, y = canvasHeight),
                                color = gridColor
                            )
                        }
                        for (i in 0..y / gridScale) {
                            drawLine(
                                start = Offset(x = 0f, y = i * cellHeight * gridScale),
                                end = Offset(x = canvasWidth, y = i * cellHeight * gridScale),
                                color = gridColor
                            )
                        }
                    }

                },
            content = content
        )
    }
}