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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun GuideScreen(modifier: Modifier = Modifier) {
    var testScore by remember { mutableFloatStateOf(21f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Column {
                Text(
                    text = "PANDUAN & RUMUS SICBO 6",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldLight,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Aturan resmi, tipe taruhan, dan kalkulator peluang 6 dadu",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        // 2. Interactive Probability Calculator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "KALKULATOR PELUANG SKOR TOTAL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val scoreInt = testScore.toInt()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pilih Nilai Total (6 - 36):", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "$scoreInt", fontSize = 20.sp, fontWeight = FontWeight.Black, color = GoldLight)
                    }

                    Slider(
                        value = testScore,
                        onValueChange = { testScore = it },
                        valueRange = 6f..36f,
                        steps = 29,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = Color(0xFF334155)
                        )
                    )

                    val isBig = scoreInt >= 21
                    val isEven = scoreInt % 2 == 0
                    val approxProb = calculateScoreProbability(scoreInt)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScoreDetailPill(label = "KATEGORI", value = if (isBig) "BESAR" else "KECIL", color = if (isBig) BigRed else SmallBlue, modifier = Modifier.weight(1f))
                        ScoreDetailPill(label = "PARITAS", value = if (isEven) "GENAP" else "GANJIL", color = if (isEven) EvenGreen else OddPurple, modifier = Modifier.weight(1f))
                        ScoreDetailPill(label = "PELUANG KELUAR", value = "${String.format("%.2f", approxProb)}%", color = GoldPrimary, modifier = Modifier.weight(1.2f))
                    }
                }
            }
        }

        // 3. Game Rules Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "ATURAN MAIN SICBO 6 DADU",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    RuleTextItem(
                        number = "1",
                        title = "Menggunakan 6 Buah Dadu",
                        desc = "Setiap periode permainan mengocok 6 dadu sekaligus. Jumlah total berkisar antara 6 (terendah) hingga 36 (tertinggi)."
                    )
                    RuleTextItem(
                        number = "2",
                        title = "Besar (Big) vs Kecil (Small)",
                        desc = "KECIL = Total 6 s/d 20. BESAR = Total 21 s/d 36. Nilai rata-rata teoretis berada tepat di angka 21."
                    )
                    RuleTextItem(
                        number = "3",
                        title = "Genap (Even) vs Ganjil (Odd)",
                        desc = "Ditentukan dari jumlah total keenam dadu apakah bernilai genap atau ganjil."
                    )
                    RuleTextItem(
                        number = "4",
                        title = "4 Dadu Sama (4 Same Dice)",
                        desc = "Jika terdapat minimal 4 dadu yang menunjukkan mata dadu bernilai identik (misal: empat angka 4), menghasilkan pembayaran hingga 100x."
                    )
                    RuleTextItem(
                        number = "5",
                        title = "Random 3D & Insurance",
                        desc = "Fitur acak pada 3 dadu depan (D1-D3) atau 3 dadu belakang (D4-D6). Dengan mengaktifkan Insurance (60% fee), pemain mendapatkan proteksi dan multiplier ekstra saat menang."
                    )
                }
            }
        }

        // 4. Paytable / Tabel Pembayaran Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TABEL PEMBAYARAN (PAYTABLE)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PaytableRow(type = "Besar / Kecil", payout = "1 : 1", chance = "~48.5%")
                    PaytableRow(type = "Genap / Ganjil", payout = "1 : 1", chance = "~50.0%")
                    PaytableRow(type = "Tebak Total Ekstrem (6-11 / 31-36)", payout = "Hingga 50x", chance = "< 2.0%")
                    PaytableRow(type = "Tebak Total Sedang (12-17 / 25-30)", payout = "15x - 25x", chance = "3% - 8%")
                    PaytableRow(type = "4 Dadu Sama (Quad)", payout = "100x", chance = "~1.5%")
                    PaytableRow(type = "Random 3D (Beruntun)", payout = "Hingga 100x Multiplier", chance = "Variatif")
                }
            }
        }

        // 5. Pattern Strategies Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "STRATEGI MEMBACA POLA",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    StrategyBullet(
                        title = "Pola Naga (Streak Road)",
                        desc = "Ketika hasil BESAR atau KECIL keluar 3 kali beruntun, biasanya kecenderungan 'mengikuti arus' berlangsung 1-3 putaran berikutnya sebelum terjadi titik balik."
                    )
                    StrategyBullet(
                        title = "Pola Selang-Seling (Ping-Pong)",
                        desc = "Hasil bergantian B-K-B-K. Jika sudah terjadi 3 pergantian, pemain berpengalaman biasanya memasang sisi sebaliknya sesuai ritme pantulan."
                    )
                    StrategyBullet(
                        title = "Reversi Rerata (Mean Reversion)",
                        desc = "Karena total kemungkinan 6 dadu bersifat simetris kurva lonceng Gauss di sekitar angka 21, streak panjang (>5x) memiliki dorongan probabilitas kuat untuk kembali ke keseimbangan."
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreDetailPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MidnightSurface)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun RuleTextItem(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(GoldPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
        }
    }
}

@Composable
fun PaytableRow(type: String, payout: String, chance: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = type, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = "Peluang: $chance", fontSize = 10.sp, color = TextMuted)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(GoldPrimary.copy(alpha = 0.15f))
                .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = payout, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight)
        }
    }
}

@Composable
fun StrategyBullet(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = "• $title", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldLight)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = desc, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
    }
}

// Approximate binomial/normal distribution probabilities for sum of 6 independent 6-sided dice
private fun calculateScoreProbability(score: Int): Float {
    // Total outcomes = 6^6 = 46656
    // Exact frequencies for sums from 6 to 36
    val frequencies = mapOf(
        6 to 1, 7 to 6, 8 to 21, 9 to 56, 10 to 126, 11 to 252,
        12 to 456, 13 to 756, 14 to 1162, 15 to 1666, 16 to 2247,
        17 to 2877, 18 to 3497, 19 to 4047, 20 to 4467, 21 to 4662,
        22 to 4662, 23 to 4467, 24 to 4047, 25 to 3497, 26 to 2877,
        27 to 2247, 28 to 1666, 29 to 1162, 30 to 756, 31 to 456,
        32 to 252, 33 to 126, 34 to 56, 35 to 21, 36 to 6
    )
    val freq = frequencies[score] ?: 1
    return (freq.toFloat() / 46656f) * 100f
}
