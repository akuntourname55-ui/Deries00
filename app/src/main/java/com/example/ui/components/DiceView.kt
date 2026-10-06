package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DiceRedFace
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PipBlack
import com.example.ui.theme.PipRed
import com.example.ui.theme.PipWhite
import com.example.ui.theme.TextMuted
import kotlin.random.Random

/**
 * High-fidelity 3D-styled Sicbo die rendered using Compose Canvas.
 * Supports authentic Red Dice or Ivory Dice with pip configurations.
 */
@Composable
fun SicboDie(
    value: Int,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isRedDie: Boolean = false
) {
    val clampedValue = value.coerceIn(1, 6)

    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, RoundedCornerShape(10.dp), spotColor = Color.Black.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = if (isRedDie) {
                        listOf(Color(0xFFEF4444), Color(0xFFDC2626), Color(0xFF991B1B))
                    } else {
                        listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                    }
                )
            )
            .border(
                width = 1.dp,
                color = if (isRedDie) Color(0xFFFCA5A5).copy(alpha = 0.4f) else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(10.dp)
            )
            .testTag("die_$clampedValue"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(size * 0.14f)) {
            val w = this.size.width
            val h = this.size.height

            // Pip color logic: Traditional Asian dice have red dot for 1 and 4, black/white for others
            val pipColor = when {
                isRedDie -> PipWhite
                clampedValue == 1 || clampedValue == 4 -> PipRed
                else -> PipBlack
            }

            val r = (w * 0.12f).coerceAtLeast(3f)

            fun drawPip(cx: Float, cy: Float, radius: Float = r, color: Color = pipColor) {
                // Pip shadow for inset 3D effect
                drawCircle(
                    color = Color.Black.copy(alpha = 0.18f),
                    radius = radius,
                    center = Offset(cx, cy + 1f)
                )
                // Pip main dot
                drawCircle(
                    color = color,
                    radius = radius,
                    center = Offset(cx, cy)
                )
            }

            val left = w * 0.22f
            val midX = w * 0.5f
            val right = w * 0.78f

            val top = h * 0.22f
            val midY = h * 0.5f
            val bottom = h * 0.78f

            when (clampedValue) {
                1 -> {
                    // Jumbo center pip for 1
                    drawPip(midX, midY, radius = r * 1.55f, color = if (isRedDie) PipWhite else PipRed)
                }
                2 -> {
                    drawPip(left, top)
                    drawPip(right, bottom)
                }
                3 -> {
                    drawPip(left, top)
                    drawPip(midX, midY)
                    drawPip(right, bottom)
                }
                4 -> {
                    drawPip(left, top, color = if (isRedDie) PipWhite else PipRed)
                    drawPip(right, top, color = if (isRedDie) PipWhite else PipRed)
                    drawPip(left, bottom, color = if (isRedDie) PipWhite else PipRed)
                    drawPip(right, bottom, color = if (isRedDie) PipWhite else PipRed)
                }
                5 -> {
                    drawPip(left, top)
                    drawPip(right, top)
                    drawPip(midX, midY)
                    drawPip(left, bottom)
                    drawPip(right, bottom)
                }
                6 -> {
                    drawPip(left, top)
                    drawPip(right, top)
                    drawPip(left, midY)
                    drawPip(right, midY)
                    drawPip(left, bottom)
                    drawPip(right, bottom)
                }
            }
        }
    }
}

/**
 * Row displaying the 6 dice from a Sicbo 6 roll, with position indicators (D1..D6).
 */
@Composable
fun SixDiceDisplay(
    dice: List<Int>,
    modifier: Modifier = Modifier,
    dieSize: Dp = 44.dp,
    showLabels: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val safeDice = if (dice.size == 6) dice else List(6) { 1 }
        safeDice.forEachIndexed { index, dieVal ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // In Sicbo 6, odd indices can be white and even red, giving visual delight
                SicboDie(
                    value = dieVal,
                    size = dieSize,
                    isRedDie = index % 2 == 1
                )
                if (showLabels) {
                    Text(
                        text = "D${index + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

/**
 * Animated Shaking Cup / Tumbler simulation for dice rolling.
 */
@Composable
fun ShakingCupVisualizer(
    isShaking: Boolean,
    modifier: Modifier = Modifier
) {
    val rotationAnim = remember { Animatable(0f) }
    val offsetXAnim = remember { Animatable(0f) }

    LaunchedEffect(isShaking) {
        if (isShaking) {
            while (true) {
                rotationAnim.animateTo(
                    targetValue = Random.nextInt(-10, 10).toFloat(),
                    animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing)
                )
                offsetXAnim.animateTo(
                    targetValue = Random.nextInt(-8, 8).toFloat(),
                    animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing)
                )
            }
        } else {
            rotationAnim.animateTo(0f)
            offsetXAnim.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
            .offset(x = offsetXAnim.value.dp)
            .rotate(rotationAnim.value),
        contentAlignment = Alignment.Center
    ) {
        // Tumbler dome
        Canvas(modifier = Modifier.size(110.dp)) {
            val w = size.width
            val h = size.height

            // Base plate
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFF475569), Color(0xFF1E293B))),
                topLeft = Offset(w * 0.05f, h * 0.78f),
                size = Size(w * 0.9f, h * 0.18f),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Transparent glass dome
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.25f),
                        Color(0xFF0284C7).copy(alpha = 0.08f)
                    ),
                    center = Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.45f
                ),
                topLeft = Offset(w * 0.15f, h * 0.15f),
                size = Size(w * 0.7f, h * 0.65f),
                cornerRadius = CornerRadius(48f, 48f)
            )

            // Gold decorative rim
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFDE68A), Color(0xFFD97706))),
                topLeft = Offset(w * 0.12f, h * 0.72f),
                size = Size(w * 0.76f, h * 0.08f),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }

        // Mini preview dice inside the tumbler
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            SicboDie(value = if (isShaking) Random.nextInt(1, 7) else 3, size = 20.dp, isRedDie = false)
            SicboDie(value = if (isShaking) Random.nextInt(1, 7) else 6, size = 20.dp, isRedDie = true)
            SicboDie(value = if (isShaking) Random.nextInt(1, 7) else 2, size = 20.dp, isRedDie = false)
        }
    }
}
