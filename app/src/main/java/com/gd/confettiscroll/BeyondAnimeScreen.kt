package com.gd.confettiscroll

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


@Composable
fun BeyondAnimeScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        // Background confetti
        InfiniteConfetti(
            confettiCount = 60,
            scrollSpeed = 30f,
            confettiColors = listOf(
                Color(0xFF4FC3F7),
                Color(0xFF81C784),
                Color(0xFFFFB74D),
                Color(0xFFE57373),
                Color(0xFFBA68C8)
            )
        )

        // Scrolling image tracks
        ScrollingImageTracks()

        // Main content with proper positioning
        MainContent()
    }
}

@Composable
private fun ScrollingImageTracks() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth > 600.dp

    val trackWidth = if (isTablet) 80.dp else 60.dp
    val trackSpacing = if (isTablet) 30.dp else 25.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 20.dp)
    ) {
        // First track (rightmost)
        ScrollingImageTrack(
            images = rightTrackImages,
            modifier = Modifier
                .width(trackWidth)
                .fillMaxHeight()
                .align(Alignment.TopEnd)
                .graphicsLayer {
                    rotationZ = 30f
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                },
            scrollDirection = ScrollDirection.Up,
            speed = 60f
        )

        // Second track (middle)
        ScrollingImageTrack(
            images = centerTrackImages,
            modifier = Modifier
                .width(trackWidth)
                .fillMaxHeight()
                .align(Alignment.TopEnd)
                .offset(x = -trackSpacing * 3)
                .graphicsLayer {
                    rotationZ = 30f
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                },
            scrollDirection = ScrollDirection.Down,
            speed = 40f
        )

        // Third track (leftmost of the three)
        ScrollingImageTrack(
            images = leftTrackImages,
            modifier = Modifier
                .width(trackWidth)
                .fillMaxHeight()
                .align(Alignment.TopEnd)
                .offset(x = -trackSpacing * 6)
                .graphicsLayer {
                    rotationZ = 30f
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                },
            scrollDirection = ScrollDirection.Up,
            speed = 60f
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ScrollingImageTrack(
    images: List<String>,
    modifier: Modifier = Modifier,
    scrollDirection: ScrollDirection,
    speed: Float
) {
    val imageHeight = 120.dp
    val spacing = 12.dp
    val density = LocalDensity.current

    // Convert to pixels once
    val imageHeightPx = with(density) { imageHeight.toPx() }
    val spacingPx = with(density) { spacing.toPx() }
    val totalItemHeightPx = imageHeightPx + spacingPx

    val containerHeightPx = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    // Calculate how many items we need for seamless loop
    val visibleItems = (containerHeightPx / totalItemHeightPx).toInt() + 2
    val totalItems = (visibleItems * 2).coerceAtLeast(images.size * 2)

    // Create continuous animation that never resets
    val infiniteTransition = rememberInfiniteTransition(label = "seamless_scroll")
    val scrollPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = totalItemHeightPx * totalItems,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ((totalItemHeightPx * totalItems) / speed * 500).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "scroll_position"
    )

    Box(modifier = modifier) {
        repeat(totalItems) { index ->
            val imageIndex = index % images.size

            // Calculate position for each item
            val basePositionPx = index.toFloat() * totalItemHeightPx
            val currentPositionPx = when (scrollDirection) {
                ScrollDirection.Up -> basePositionPx - scrollPosition
                ScrollDirection.Down -> scrollPosition - basePositionPx
            }

            // Create seamless wrapping
            val wrappedPositionPx = ((currentPositionPx % (totalItemHeightPx * totalItems)) +
                    (totalItemHeightPx * totalItems)) % (totalItemHeightPx * totalItems) -
                    totalItemHeightPx

            // Only render if potentially visible (with buffer)
            if (wrappedPositionPx > -totalItemHeightPx * 2 &&
                wrappedPositionPx < containerHeightPx + totalItemHeightPx * 2) {

                val offsetDp = with(density) { wrappedPositionPx.toDp() }

                AnimeImageCard(
                    imageUrl = images[imageIndex],
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .offset(y = offsetDp)
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimeImageCard(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.05f)
                        ),
                        radius = 300f
                    )
                ).border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for anime character with glass backdrop
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎭",
                    fontSize = 28.sp,
                    color = Color.White
                )
            }

             AsyncImage(
                 model = imageUrl,
                 contentDescription = null,
                 modifier = Modifier.fillMaxSize(),
                 contentScale = ContentScale.Crop
             )

            //Overlay for glass effect over real images
             Box(
                 modifier = Modifier
                     .fillMaxSize()
                     .background(Color.White.copy(alpha = 0.1f))
             )
        }
    }
}

@Composable
private fun MainContent() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth > 600.dp

    val horizontalPadding = if (isTablet) 32.dp else 24.dp
    val titleFontSize = if (isTablet) 40.sp else 32.sp
    val bodyFontSize = if (isTablet) 16.sp else 14.sp

    // Content positioned on the left side with translucent black wrapper
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = horizontalPadding, end = if(isTablet) 380.dp else 140.dp)
            .padding(vertical = 32.dp)
    ) {
        // Translucent black background wrapper
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                // Main title with gradient effect
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF4FC3F7),
                                    Color(0xFF81C784)
                                )
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Beyond Anime",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = titleFontSize,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description text with left alignment
                Text(
                    text = "Explore a universe where every story comes to life, from epic battles to heartfelt journeys. Discover anime series and movies that ignite your imagination and leave a lasting impact.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = bodyFontSize,
                        lineHeight = (bodyFontSize.value * 1.5).sp
                    ),
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Get Started button
                Button(
                    onClick = { /* Handle click */ },
                    modifier = Modifier
                        .height(if (isTablet) 56.dp else 48.dp)
                        .widthIn(min = if (isTablet) 200.dp else 160.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = if (isTablet) 16.sp else 14.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

enum class ScrollDirection {
    Up, Down
}


private val leftTrackImages = listOf(
    "https://images.stockcake.com/public/4/f/6/4f639584-f353-489b-a3c8-e44b29032cc1_large/focused-anime-professional-stockcake.jpg",
    "https://images.stockcake.com/public/8/1/1/8113d2ba-dda0-4f76-bbf0-219de62703f7_large/classic-anime-character-stockcake.jpg",
    "https://images.stockcake.com/public/6/1/2/612aaebd-2e7e-42af-a528-d517740f65e8_large/speed-rivals-clash-stockcake.jpg",
    "https://images.stockcake.com/public/9/1/b/91bc458b-8064-4f3a-a68e-1883513e15d5_large/soaring-military-commander-stockcake.jpg",
    "https://images.stockcake.com/public/e/0/3/e03e26c2-344a-4399-88ad-b5408cc7648c_large/dynamic-jersey-athlete-stockcake.jpg"
)

private val rightTrackImages = listOf(
    "https://images.stockcake.com/public/7/7/1/7713a670-addc-4109-9a90-e27410bcdace_large/wistful-anime-portrait-stockcake.jpg",
    "https://images.stockcake.com/public/c/4/c/c4cad9cf-5b3f-46ec-95ec-bd5888f47812_large/joyful-sakura-leap-stockcake.jpg",
    "https://images.stockcake.com/public/f/7/c/f7c8796c-5f6f-4c5c-a975-8ff63fa0d1ac_large/heroic-anime-commander-stockcake.jpg",
    "https://images.stockcake.com/public/e/0/9/e0988aed-1629-4b2d-838d-0fbd0d775982_large/dawn-of-determination-stockcake.jpg",
    "https://images.stockcake.com/public/9/9/5/995ec20e-eee1-4b62-8cf2-96e003eef47f_large/skyward-ambition-rising-stockcake.jpg"
)

private val centerTrackImages = listOf(
    "https://images.stockcake.com/public/6/4/8/648d062a-6192-4d49-abc6-86f9c4545cb3_large/adorable-animal-stack-stockcake.jpg",
    "https://images.stockcake.com/public/7/d/5/7d51cd7e-b9eb-4d1d-8ce0-1fa9316166b1_large/scientist-meets-rodents-stockcake.jpg",
    "https://images.stockcake.com/public/5/c/d/5cd0cd89-2c1b-458c-b175-43938a1b8036_large/vibrant-rural-harmony-stockcake.jpg",
    "https://images.stockcake.com/public/4/5/1/4516c83c-deb9-42af-ba41-c71459e5bd0c_large/airborne-anime-warrior-stockcake.jpg",
    "https://images.stockcake.com/public/b/0/5/b0561cbd-38c6-4cdf-bf93-3e05b4d1da47_large/candlelit-gothic-beauty-stockcake.jpg"
)

