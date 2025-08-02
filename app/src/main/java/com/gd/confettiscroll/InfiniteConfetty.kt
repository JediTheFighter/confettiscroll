package com.gd.confettiscroll

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random

@Composable
fun InfiniteConfettiScroll(
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

// Enhanced version with different shapes
@Composable
fun EnhancedConfettiScroll(
    modifier: Modifier = Modifier,
    confettiCount: Int = 60
) {
    val confettiShapes = listOf(
        ConfettiShape.Rectangle,
        ConfettiShape.Circle,
        ConfettiShape.Triangle,
        ConfettiShape.Star
    )

    val confettiParticles = remember(confettiCount) {
        (0 until confettiCount).map { index ->
            EnhancedConfettiParticle(
                id = index,
                x = Random.nextFloat(),
                y = Random.nextFloat() * 2f - 1f,
                color = Color.hsv(Random.nextFloat() * 360f, 0.8f, 0.9f),
                size = Random.nextFloat() * 10f + 6f,
                shape = confettiShapes.random(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 6f - 3f,
                fallSpeed = Random.nextFloat() * 1.5f + 0.8f,
                swayAmplitude = Random.nextFloat() * 30f + 10f,
                swayFrequency = Random.nextFloat() * 2f + 1f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "enhanced_confetti")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        confettiParticles.forEach { particle ->
            val progress = (time * particle.fallSpeed) % 1f
            val currentY = -50f + progress * (size.height + 100f)

            // Add horizontal sway
            val sway = sin(time * particle.swayFrequency * 2 * PI) * particle.swayAmplitude
            val currentX = particle.x * size.width + sway.toFloat()

            val currentRotation = particle.rotation + time * 360f * particle.rotationSpeed

            drawContext.canvas.save()
            drawContext.canvas.translate(currentX, currentY)
            drawContext.canvas.rotate(currentRotation)

            when (particle.shape) {
                ConfettiShape.Rectangle -> {
                    drawRect(
                        color = particle.color,
                        topLeft = Offset(-particle.size / 2, -particle.size / 3),
                        size = Size(particle.size, particle.size * 0.6f)
                    )
                }
                ConfettiShape.Circle -> {
                    drawCircle(
                        color = particle.color,
                        radius = particle.size / 2
                    )
                }
                ConfettiShape.Triangle -> {
                    val path = Path().apply {
                        moveTo(0f, -particle.size / 2)
                        lineTo(-particle.size / 2, particle.size / 2)
                        lineTo(particle.size / 2, particle.size / 2)
                        close()
                    }
                    drawPath(path, particle.color)
                }
                ConfettiShape.Star -> {
                    val path = createStarPath(particle.size / 2)
                    drawPath(path, particle.color)
                }
            }

            drawContext.canvas.restore()
        }
    }
}

fun createStarPath(radius: Float): Path {
    val path = Path()
    val angle = PI / 5

    for (i in 0 until 10) {
        val r = if (i % 2 == 0) radius else radius * 0.4f
        val x = (r * cos(i * angle - PI / 2)).toFloat()
        val y = (r * sin(i * angle - PI / 2)).toFloat()

        if (i == 0) path.moveTo(x, y)
        else path.lineTo(x, y)
    }
    path.close()
    return path
}

data class EnhancedConfettiParticle(
    val id: Int,
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val shape: ConfettiShape,
    val rotation: Float,
    val rotationSpeed: Float,
    val fallSpeed: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float
)

enum class ConfettiShape {
    Rectangle, Circle, Triangle, Star
}
