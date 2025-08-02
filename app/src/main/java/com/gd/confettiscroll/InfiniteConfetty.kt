package com.gd.confettiscroll

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.*
import kotlin.random.Random

@Composable
fun InfiniteConfetti(
    modifier: Modifier = Modifier,
    confettiCount: Int = 50,
    scrollSpeed: Float = 100f,
    confettiColors: List<Color> = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow,
        Color.Magenta, Color.Cyan, Color(0xFFFF5722)
    )
) {
    val density = LocalDensity.current
    var screenHeight by remember { mutableFloatStateOf(0f) }
    var screenWidth by remember { mutableFloatStateOf(0f) }

    // Generate confetti particles
    val confettiParticles = remember(confettiCount) {
        (0 until confettiCount).map { index ->
            ConfettiParticle(
                id = index,
                x = Random.nextFloat(),
                y = Random.nextFloat() * 2f - 1f, // Start some above screen
                color = confettiColors.random(),
                size = Random.nextFloat() * 8f + 4f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 4f - 2f,
                fallSpeed = Random.nextFloat() * 2f + 1f
            )
        }
    }

    // Animation for continuous scrolling
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (10000 / scrollSpeed * 100).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "scroll"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                screenHeight = size.height.toFloat()
                screenWidth = size.width.toFloat()
            }
    ) {
        if (screenHeight == 0f || screenWidth == 0f) return@Canvas

        confettiParticles.forEach { particle ->
            val currentY = (particle.y * screenHeight +
                    animationProgress * screenHeight * particle.fallSpeed) %
                    (screenHeight + 100f) - 50f

            val currentX = particle.x * screenWidth
            val currentRotation = particle.rotation +
                    animationProgress * 360f * particle.rotationSpeed

            // Draw confetti piece (rectangle with rotation)
            drawContext.canvas.save()
            drawContext.canvas.translate(currentX, currentY)
            drawContext.canvas.rotate(currentRotation)

            drawRect(
                color = particle.color,
                topLeft = Offset(-particle.size / 2, -particle.size / 2),
                size = Size(particle.size, particle.size * 0.6f)
            )

            drawContext.canvas.restore()
        }
    }
}

data class ConfettiParticle(
    val id: Int,
    val x: Float, // 0 to 1 (percentage of screen width)
    val y: Float, // Initial Y position
    val color: Color,
    val size: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val fallSpeed: Float
)
