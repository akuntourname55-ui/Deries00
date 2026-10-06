package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.ui.theme.BigRed
import com.example.ui.theme.EvenGreen
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceCard
import com.example.ui.theme.OddPurple
import com.example.ui.theme.SmallBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Bead Plate (Paito Kotak Matriks) - 6 rows high, scrollable horizontally.
 * Displays result circle with B/K, total score, and G/G indicator.
 */
@Composable
fun BeadPlateView(
    periods: List<SicboPeriod>,
    modifier: Modifier = Modifier,
    maxRounds: Int = 60
) {
    val chronological = remember(periods) {
        periods.take(maxRounds).reversed()
    }

    val rows = 6
    val columns = (chronological.size + rows - 1) / rows

    val scrollState = rememberScrollState()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BEAD PLATE (PAITO MATRIKS)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                letterSpacing = 1.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LegendBadge(label = "B: Besar", color = BigRed)
                LegendBadge(label = "K: Kecil", color = SmallBlue)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MidnightSurface)
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until columns.coerceAtLeast(10)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (row in 0 until rows) {
                            val index = col * rows + row
                            if (index < chronological.size) {
                                val item = chronological[index]
                                BeadPlateCell(item = item)
                            } else {
                                EmptyGridCell()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BeadPlateCell(item: SicboPeriod) {
    val bg = if (item.isBig) BigRed else SmallBlue
    val dotBorder = if (item.isEven) EvenGreen else OddPurple

    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.5.dp, dotBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (item.isBig) "B" else "K",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = item.total.toString(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun EmptyGridCell() {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xFF0F172A).copy(alpha = 0.6f))
            .border(0.5.dp, Color(0xFF1E293B), CircleShape)
    )
}

/**
 * Big Road (Pola Naga / Streak Columns)
 * Groups consecutive runs of Besar or Kecil into columns.
 */
@Composable
fun BigRoadView(
    periods: List<SicboPeriod>,
    modifier: Modifier = Modifier,
    maxRounds: Int = 60
) {
    val columns = remember(periods) {
        val list = periods.take(maxRounds).reversed()
        val cols = mutableListOf<MutableList<SicboPeriod>>()
        var currentCol = mutableListOf<SicboPeriod>()

        for (item in list) {
            if (currentCol.isEmpty()) {
                currentCol.add(item)
            } else if (currentCol.first().isBig == item.isBig) {
                currentCol.add(item)
            } else {
                cols.add(currentCol)
                currentCol = mutableListOf(item)
            }
        }
        if (currentCol.isNotEmpty()) cols.add(currentCol)
        cols
    }

    val scrollState = rememberScrollState()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BIG ROAD (POLA NAGA & TREN)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "${columns.size} Kolom Streak",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MidnightSurface)
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                columns.forEach { colItems ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        colItems.forEach { item ->
                            BigRoadRing(isBig = item.isBig, total = item.total)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BigRoadRing(isBig: Boolean, total: Int) {
    val color = if (isBig) BigRed else SmallBlue

    Box(
        modifier = Modifier
            .size(28.dp)
            .border(2.5.dp, color, CircleShape)
            .background(color.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = total.toString(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * Dice Frequency Distribution Bars (1 to 6 heatmap)
 */
@Composable
fun DiceFrequencyChart(
    stats: PatternStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISTRIBUSI MATA DADU (1-6)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagBadge(label = "HOT: ⚂ ${stats.hotDice}", color = BigRed)
                    TagBadge(label = "COLD: ⚅ ${stats.coldDice}", color = SmallBlue)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val totalDiceRolled = (stats.totalRounds * 6).coerceAtLeast(1)

            for (diceVal in 1..6) {
                val count = stats.diceCounts[diceVal] ?: 0
                val percent = (count.toFloat() / totalDiceRolled) * 100f
                val progress = (percent / 25f).coerceIn(0f, 1f) // standard is ~16.6%

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = diceVal.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = when (diceVal) {
                            stats.hotDice -> BigRed
                            stats.coldDice -> SmallBlue
                            else -> GoldPrimary
                        },
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "$count (${String.format("%.1f", percent)}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.width(76.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun LegendBadge(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
fun TagBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
