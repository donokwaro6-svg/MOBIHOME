package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MobiCoralPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Animated opening splash screen displayed when someone opens the app.
 * Prominently showcases the App Name ("MobiHome"), the custom icon/logo,
 * and the slogan 'FindSpace' directly beneath them.
 */
@Composable
fun OpeningSplashScreen(
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logoScale = remember { Animatable(0.25f) }
    val logoAlpha = remember { Animatable(0f) }
    val logoRotation = remember { Animatable(-14f) }

    val nameAlpha = remember { Animatable(0f) }
    val nameOffsetY = remember { Animatable(45f) }

    val sloganAlpha = remember { Animatable(0f) }
    val sloganOffsetY = remember { Animatable(32f) }
    val sloganScale = remember { Animatable(0.85f) }

    val footerAlpha = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0f) }

    // Ambient breathing pulse around the logo icon
    val infiniteTransition = rememberInfiniteTransition(label = "ambientPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    LaunchedEffect(Unit) {
        // Stage 1: Logo enters with an elastic spring and smooth rotation
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            logoRotation.animateTo(
                0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            logoScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        delay(320)

        // Stage 2: App Name slides up and fades in
        launch {
            nameAlpha.animateTo(1f, animationSpec = tween(450))
        }
        launch {
            nameOffsetY.animateTo(
                0f,
                animationSpec = spring(
                    dampingRatio = 0.75f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }

        delay(260)

        // Stage 3: Slogan 'FindSpace' slides up into position under the app name
        launch {
            sloganAlpha.animateTo(1f, animationSpec = tween(450))
        }
        launch {
            sloganOffsetY.animateTo(
                0f,
                animationSpec = spring(
                    dampingRatio = 0.72f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            sloganScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f))
        }

        // Stage 4: Subtitle footer & progress line reveal
        launch {
            footerAlpha.animateTo(1f, animationSpec = tween(400))
        }
        launch {
            progressAnim.animateTo(1f, animationSpec = tween(1300, easing = FastOutSlowInEasing))
        }

        // Stage 5: Conclude sequence after comfortable reading time (~2.3s total)
        delay(1450)
        onAnimationFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0E14),
                        Color(0xFF151622),
                        Color(0xFF090A0E)
                    )
                )
            )
            .clickable { onAnimationFinished() }
            .testTag("opening_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient radial background glow behind the logo
        Box(
            modifier = Modifier
                .size(320.dp)
                .graphicsLayer {
                    alpha = glowAlpha * 0.8f
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MobiCoralPrimary.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Center Column: Logo -> App Name -> Slogan
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            // 1. App Icon / Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer {
                        val currentScale = logoScale.value * (if (logoScale.value >= 0.95f) pulseScale else 1f)
                        scaleX = currentScale
                        scaleY = currentScale
                        alpha = logoAlpha.value
                        rotationZ = logoRotation.value
                    }
                    .testTag("splash_logo")
            ) {
                // Outer subtle glowing aura ring
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .graphicsLayer {
                            scaleX = pulseScale * 1.15f
                            scaleY = pulseScale * 1.15f
                            alpha = glowAlpha
                        }
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MobiCoralPrimary.copy(alpha = 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // High-polish Squircle Brand Container
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color.Transparent,
                    shadowElevation = 20.dp,
                    tonalElevation = 8.dp,
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.32f)),
                    modifier = Modifier.size(98.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MobiCoralPrimary,
                                        Color(0xFFFF5252),
                                        Color(0xFFFF7A59)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HomeWork,
                            contentDescription = "MobiHome Logo Icon",
                            tint = Color.White,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 2. App Name: "MobiHome"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = nameAlpha.value
                        translationY = nameOffsetY.value
                    }
                    .testTag("splash_app_name")
            ) {
                Text(
                    text = "MobiHome",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = ".",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MobiCoralPrimary,
                    letterSpacing = (-0.5).sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Slogan 'FindSpace' directly under the App Name and Icon/Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = sloganAlpha.value
                        translationY = sloganOffsetY.value
                        scaleX = sloganScale.value
                        scaleY = sloganScale.value
                    }
                    .testTag("splash_slogan")
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MobiCoralPrimary.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, MobiCoralPrimary.copy(alpha = 0.45f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MobiCoralPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FindSpace",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MobiCoralPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Homes · Workspaces · Commercial Stays",
                    color = Color.White.copy(alpha = 0.62f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom Progress Indicator & Tap-to-Enter Hint
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
                .graphicsLayer { alpha = footerAlpha.value },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.16f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnim.value)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(MobiCoralPrimary, Color(0xFFFF8A65))
                            )
                        )
                )
            }

            Text(
                text = "Tap anywhere to continue",
                color = Color.White.copy(alpha = 0.42f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp
            )
        }
    }
}
