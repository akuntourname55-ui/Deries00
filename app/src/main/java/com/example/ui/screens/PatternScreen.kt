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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PatternStats
import com.example.model.SicboPeriod
import com.example.ui.components.BeadPlateView
import com.example.ui.components.BigRoadView
import com.example.ui.components.DiceFrequencyChart
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
fun PatternScreen(
    periods: List<SicboPeriod>,
    stats: PatternStats,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Title Banner
        item {
            Column {
                Text(
                    text = "POLA PENGELUARAN & ROADMAP",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldLight,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Berdasarkan ${stats.totalRounds} periode terakhir",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        // 2. Big vs Small & Even vs Odd Distribution Ratio Card
        item {
            RatioOverviewCard(stats = stats)
        }

        // 3. Bead Plate (Paito Kotak)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    BeadPlateView(periods = periods, maxRounds = 60)
                }
            }
        }

        // 4. Big Road (Pola Naga Streak)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    BigRoadView(periods = periods, maxRounds = 60)
                }
            }
        }

        // 5. Streak Records Card
        item {
            StreakRecordsCard(stats = stats)
        }

        // 6. Dice Heatmap (1-6)
        item {
            DiceFrequencyChart(stats = stats)
        }

        // 7. Position Averages (D1 to D6)
        item {
            PositionAveragesCard(stats = stats)
        }
    }
}

@Composable
fun RatioOverviewCard(stats: PatternStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceElevated),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RASIO & PERSENTASE HASIL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Big vs Small Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "BESAR: ${stats.bigCount} (${String.format("%.1f", stats.bigPercent)}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BigRed
                )
                Text(
                    text = "KECIL: ${stats.smallCount} (${String.format("%.1f", stats.smallPercent)}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SmallBlue
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val bigRatio = (stats.bigPercent / 100f).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { bigRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = BigRed,
                trackColor = SmallBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Even vs Odd Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "GENAP: ${stats.evenCount} (${String.format("%.1f", stats.evenPercent)}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvenGreen
                )
                Text(
                    text = "GANJIL: ${stats.oddCount} (${String.format("%.1f", stats.oddPercent)}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OddPurple
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val evenRatio = (stats.evenPercent / 100f).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { evenRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = EvenGreen,
                trackColor = OddPurple
            )
        }
    }
}

@Composable
fun StreakRecordsCard(stats: PatternStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "REKOR STREAK BERUNTUN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StreakStatBox(title = "NAGA BESAR", value = "${stats.longestBigStreak}x", color = BigRed, modifier = Modifier.weight(1f))
                StreakStatBox(title = "NAGA KECIL", value = "${stats.longestSmallStreak}x", color = SmallBlue, modifier = Modifier.weight(1f))
                StreakStatBox(title = "NAGA GENAP", value = "${stats.longestEvenStreak}x", color = EvenGreen, modifier = Modifier.weight(1f))
                StreakStatBox(title = "NAGA GANJIL", value = "${stats.longestOddStreak}x", color = OddPurple, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StreakStatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MidnightSurface)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun PositionAveragesCard(stats: PatternStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RATA-RATA POSISI DADU (D1 - D6)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Text(
                    text = "Rerata Total: ${String.format("%.1f", stats.averageTotal)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stats.positionAverages.forEachIndexed { index, avg ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MidnightSurface)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "D${index + 1}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format("%.2f", avg),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}
