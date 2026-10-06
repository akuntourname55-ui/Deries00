package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PatternStats
import com.example.model.SicboPeriod
import com.example.ui.components.BeadPlateView
import com.example.ui.components.ShakingCupVisualizer
import com.example.ui.components.SixDiceDisplay
import com.example.ui.theme.BigRed
import com.example.ui.theme.EvenGreen
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceCard
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.OddPurple
import com.example.ui.theme.SmallBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LiveBoardScreen(
    latestPeriod: SicboPeriod?,
    allPeriods: List<SicboPeriod>,
    stats: PatternStats,
    isShaking: Boolean,
    isAutoRoll: Boolean,
    countdown: Int,
    onRollManual: () -> Unit,
    onToggleAutoRoll: () -> Unit,
    onOpenCustomDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live Period Header & Countdown
        item {
            LiveHeaderCard(
                periodNumber = latestPeriod?.periodNumber ?: "---",
                countdown = countdown,
                isAutoRoll = isAutoRoll
            )
        }

        // 2. Interactive Dice Shaker & Latest Result Card
        item {
            MainResultCard(
                period = latestPeriod,
                isShaking = isShaking,
                onRollManual = onRollManual,
                onToggleAutoRoll = onToggleAutoRoll,
                onOpenCustomDialog = onOpenCustomDialog,
                isAutoRoll = isAutoRoll
            )
        }

        // 3. Quick Prediction & Streak Alert Card
        item {
            PredictionBannerCard(stats = stats)
        }

        // 4. Bead Plate Mini-Road
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    BeadPlateView(periods = allPeriods, maxRounds = 36)
                }
            }
        }
    }
}

@Composable
fun LiveHeaderCard(
    periodNumber: String,
    countdown: Int,
    isAutoRoll: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.5f), Color.Transparent)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                    )
                    Text(
                        text = "SICBO 6 DICE • LIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF22C55E),
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Periode #$periodNumber",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }

            // Countdown Ring / Box
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isAutoRoll) "NEXT ROLL IN" else "TIMER PAUSED",
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (countdown <= 5) BigRed.copy(alpha = 0.2f) else MidnightSurface)
                        .border(
                            1.dp,
                            if (countdown <= 5) BigRed else GoldPrimary.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "00:${String.format("%02d", countdown)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (countdown <= 5) BigRed else GoldLight
                    )
                }
            }
        }
    }
}

@Composable
fun MainResultCard(
    period: SicboPeriod?,
    isShaking: Boolean,
    isAutoRoll: Boolean,
    onRollManual: () -> Unit,
    onToggleAutoRoll: () -> Unit,
    onOpenCustomDialog: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dice cup visualizer
            ShakingCupVisualizer(
                isShaking = isShaking,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (isShaking) {
                Text(
                    text = "Mengocok 6 Dadu...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                // 6 Dice Row
                SixDiceDisplay(
                    dice = period?.dice ?: listOf(1, 2, 3, 4, 5, 6),
                    dieSize = 46.dp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Outcome Badges (TOTAL, BESAR/KECIL, GENAP/GANJIL)
            if (period != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Total Box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MidnightSurfaceElevated)
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "TOTAL", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Text(text = "${period.total}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                    }

                    // Big / Small Box
                    val bsBg = if (period.isBig) BigRed else SmallBlue
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bsBg.copy(alpha = 0.2f))
                            .border(1.5.dp, bsBg, RoundedCornerShape(10.dp))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (period.isBig) "BESAR (21-36)" else "KECIL (6-20)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = bsBg
                            )
                        }
                    }

                    // Even / Odd Box
                    val oeBg = if (period.isEven) EvenGreen else OddPurple
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(oeBg.copy(alpha = 0.2f))
                            .border(1.5.dp, oeBg, RoundedCornerShape(10.dp))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (period.isEven) "GENAP" else "GANJIL",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = oeBg
                            )
                        }
                    }
                }

                // Special 4D & 3D Badges if any
                if (period.same4Dice != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldPrimary.copy(alpha = 0.2f))
                            .border(1.dp, GoldPrimary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "★ SPESIAL: 4 DADU SAMA bernilai [ ${period.same4Dice} ] (x100 Multiplier)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRollManual,
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("roll_manual_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = MidnightBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Kocok Sekarang", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onToggleAutoRoll,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("toggle_auto_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isAutoRoll) Color(0xFF22C55E) else TextMuted
                    )
                ) {
                    Icon(
                        imageVector = if (isAutoRoll) Icons.Default.PlayArrow else Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isAutoRoll) "Auto: ON" else "Auto: OFF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenCustomDialog,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_custom_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "+ Data", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PredictionBannerCard(stats: PatternStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Brush.horizontalGradient(listOf(GoldPrimary.copy(alpha = 0.6f), Color(0xFF3B82F6).copy(alpha = 0.4f))))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "PREDIKSI & ANALISIS POLA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Akurasi: ${stats.prediction.bsConfidence}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Streak Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MidnightSurface)
                        .padding(8.dp)
                ) {
                    Column {
                        Text(text = "STREAK AKTIF", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = "${stats.currentStreakCount}x ${stats.currentStreakType}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (stats.currentStreakType == "BESAR") BigRed else SmallBlue
                        )
                    }
                }

                // Recommendation Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MidnightSurface)
                        .padding(8.dp)
                ) {
                    Column {
                        Text(text = "TARGET BERIKUTNYA", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = stats.prediction.bsRecommendation,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (stats.prediction.bsRecommendation == "BESAR") BigRed else SmallBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stats.prediction.bsReason,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
