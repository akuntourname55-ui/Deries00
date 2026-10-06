package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameStatistics
import com.example.model.SicboPeriod
import com.example.ui.components.SicboDie
import com.example.ui.theme.ActionClear
import com.example.ui.theme.ActionRebet
import com.example.ui.theme.ActionSubmit
import com.example.ui.theme.ActionUndo
import com.example.ui.theme.BadgeCyan
import com.example.ui.theme.BadgeYellowGreen
import com.example.ui.theme.BetButtonTeal
import com.example.ui.theme.BetButtonTealBorder
import com.example.ui.theme.BetButtonTealText
import com.example.ui.theme.BetButtonWinLime
import com.example.ui.theme.BetButtonWinText
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PipBlack
import com.example.ui.theme.PipRed
import com.example.ui.theme.TableBackground
import com.example.ui.theme.TableFeltBorder
import com.example.ui.theme.TableFeltGreen
import com.example.ui.theme.TablePanelBg
import com.example.ui.theme.TablePanelBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.random.Random

/**
 * Screen rendering the EXACT game table UI from game.yukmainlottery.com (Sicbo 6 Dice).
 */
@Composable
fun SicboGameTableView(
    latestPeriod: SicboPeriod?,
    allPeriods: List<SicboPeriod>,
    gameStats: GameStatistics,
    countdown: Int,
    isShaking: Boolean,
    onRollManual: () -> Unit,
    onOpenHistoryModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAudioOn by remember { mutableStateOf(true) }
    var random3DSequence by remember { mutableStateOf(listOf(3, 5, 1)) }
    var selectedChipValue by remember { mutableStateOf("10K") }
    val placedBets = remember { mutableStateMapOf<String, String>() }
    var balance by remember { mutableStateOf(277.00) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TableBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        // 1. Top Header Bar
        TopUserInfoBar(
            username = "deries00",
            balance = balance,
            periodNumber = latestPeriod?.periodNumber ?: "---"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Side-by-Side Panels: RESULT HISTORY & STATISTIC
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Result History Panel (Left)
            ResultHistoryPanel(
                periods = allPeriods,
                modifier = Modifier.weight(1.05f)
            )

            // Statistic Panel (Right)
            StatisticPanel(
                stats = gameStats,
                modifier = Modifier.weight(0.95f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Center Circular Dice Bowl Arena
        CenterDiceBowlArena(
            period = latestPeriod,
            countdown = countdown,
            isShaking = isShaking,
            isAudioOn = isAudioOn,
            onToggleAudio = { isAudioOn = !isAudioOn },
            onOpenHistory = onOpenHistoryModal,
            onBowlClick = onRollManual
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Result Metrics Bar (underneath bowl)
        if (latestPeriod != null) {
            ResultMetricsBar(period = latestPeriod)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Betting Board Grid
        BettingBoardGrid(
            latestPeriod = latestPeriod,
            placedBets = placedBets,
            random3DSequence = random3DSequence,
            onRefreshRandom3D = {
                random3DSequence = List(3) { Random.nextInt(1, 7) }
            },
            onPlaceBet = { betKey ->
                placedBets[betKey] = selectedChipValue
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 6. Chips Selector Row
        ChipsSelectorRow(
            selectedChip = selectedChipValue,
            onSelectChip = { selectedChipValue = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 7. Action Buttons (UNDO, CLEAR, REBET, SUBMIT)
        ActionButtonsRow(
            onUndo = {
                if (placedBets.isNotEmpty()) {
                    val lastKey = placedBets.keys.last()
                    placedBets.remove(lastKey)
                }
            },
            onClear = { placedBets.clear() },
            onRebet = {
                placedBets["BIG"] = "10K"
                placedBets["EVEN"] = "10K"
            },
            onSubmit = {
                onRollManual()
            }
        )
    }
}

/**
 * Top User & Period bar matching screenshot.
 */
@Composable
fun TopUserInfoBar(
    username: String,
    balance: Double,
    periodNumber: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF140B06))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF2B170D))
                .border(1.dp, Color(0xFF4A2B18), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "Hi, $username",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Balance Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF2B170D))
                .border(1.dp, Color(0xFF4A2B18), RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 3.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🪙", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.2f", balance),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldLight
                )
            }
        }

        // Period Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF2B170D))
                .border(1.dp, Color(0xFF4A2B18), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "Periode: #$periodNumber",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )
        }
    }
}

/**
 * Left Panel: RESULT HISTORY with mini 6-dice columns + B/S and E/O square badges.
 */
@Composable
fun ResultHistoryPanel(
    periods: List<SicboPeriod>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val recent = remember(periods) { periods.take(12).reversed() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(TablePanelBg)
            .border(1.dp, TablePanelBorder, RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column {
            Text(
                text = "RESULT HISTORY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF22D3EE),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                recent.forEach { period ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // 6 Mini Dice in 2 columns of 3
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF0F2620))
                                .border(0.5.dp, Color(0xFF1E4D40), RoundedCornerShape(4.dp))
                                .padding(2.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                    MiniDie(value = period.dice.getOrElse(0) { 1 })
                                    MiniDie(value = period.dice.getOrElse(1) { 2 })
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                    MiniDie(value = period.dice.getOrElse(2) { 3 })
                                    MiniDie(value = period.dice.getOrElse(3) { 4 })
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                    MiniDie(value = period.dice.getOrElse(4) { 5 })
                                    MiniDie(value = period.dice.getOrElse(5) { 6 })
                                }
                            }
                        }

                        // Bottom badges row: B/S and E/O
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            // BS Badge
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(BadgeYellowGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (period.isBig) "B" else "S",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }

                            // EO Badge
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(BadgeCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (period.isEven) "E" else "O",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniDie(value: Int) {
    Box(
        modifier = Modifier
            .size(13.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(1.5f.dp)) {
            val w = size.width
            val r = (w * 0.12f).coerceAtLeast(1.2f)
            val pipColor = if (value == 1 || value == 4) PipRed else PipBlack

            fun p(cx: Float, cy: Float, col: Color = pipColor) {
                drawCircle(color = col, radius = r, center = Offset(cx, cy))
            }

            when (value) {
                1 -> p(w * 0.5f, w * 0.5f, PipRed)
                2 -> { p(w * 0.25f, w * 0.25f); p(w * 0.75f, w * 0.75f) }
                3 -> { p(w * 0.25f, w * 0.25f); p(w * 0.5f, w * 0.5f); p(w * 0.75f, w * 0.75f) }
                4 -> {
                    p(w * 0.25f, w * 0.25f, PipRed); p(w * 0.75f, w * 0.25f, PipRed)
                    p(w * 0.25f, w * 0.75f, PipRed); p(w * 0.75f, w * 0.75f, PipRed)
                }
                5 -> {
                    p(w * 0.25f, w * 0.25f); p(w * 0.75f, w * 0.25f)
                    p(w * 0.5f, w * 0.5f)
                    p(w * 0.25f, w * 0.75f); p(w * 0.75f, w * 0.75f)
                }
                6 -> {
                    p(w * 0.25f, w * 0.25f); p(w * 0.75f, w * 0.25f)
                    p(w * 0.25f, w * 0.5f); p(w * 0.75f, w * 0.5f)
                    p(w * 0.25f, w * 0.75f); p(w * 0.75f, w * 0.75f)
                }
            }
        }
    }
}

/**
 * Right Panel: STATISTIC Table matching screenshot.
 */
@Composable
fun StatisticPanel(
    stats: GameStatistics,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(TablePanelBg)
            .border(1.dp, TablePanelBorder, RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column {
            Text(
                text = "STATISTIC",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF22D3EE),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Sub-header 1: SMALL | TIE | BIG
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(62.dp))
                Text(text = "SMALL", fontSize = 8.sp, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                Text(text = "TIE", fontSize = 8.sp, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                Text(text = "BIG", fontSize = 8.sp, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }

            // Row 1: SMALL/BIG
            StatRow3(label = "SMALL/BIG", v1 = "${stats.smallPct}%", v2 = "${stats.tiePct}%", v3 = "${stats.bigPct}%")
            // Row 2: 4D SMALL/BIG
            StatRow3(label = "4D SMALL/BIG", v1 = "${stats.fourDSmallPct}%", v2 = "${stats.fourDTiePct}%", v3 = "${stats.fourDBigPct}%")

            Spacer(modifier = Modifier.height(3.dp))

            // Sub-header 2: ODD | EVEN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(62.dp))
                Text(text = "ODD", fontSize = 8.sp, color = TextMuted, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                Text(text = "EVEN", fontSize = 8.sp, color = TextMuted, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
            }

            // Row 3: ODD/EVEN
            StatRow2(label = "ODD/EVEN", v1 = "${stats.oddPct}%", v2 = "${stats.evenPct}%")
            // Row 4: 4D ODD/EVEN
            StatRow2(label = "4D ODD/EVEN", v1 = "${stats.fourDOddPct}%", v2 = "${stats.fourDEvenPct}%")
        }
    }
}

@Composable
fun StatRow3(label: String, v1: String, v2: String, v3: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.width(62.dp))
        Text(text = v1, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Text(text = v2, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Text(text = v3, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
fun StatRow2(label: String, v1: String, v2: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.width(62.dp))
        Text(text = v1, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
        Text(text = v2, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
    }
}

/**
 * Center Circular Bowl Arena with 6 dice & countdown badge.
 */
@Composable
fun CenterDiceBowlArena(
    period: SicboPeriod?,
    countdown: Int,
    isShaking: Boolean,
    isAudioOn: Boolean,
    onToggleAudio: () -> Unit,
    onOpenHistory: () -> Unit,
    onBowlClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer bowl circular ring
        Box(
            modifier = Modifier
                .size(310.dp)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF5A3119),
                            Color(0xFF381F10),
                            Color(0xFF1E1109)
                        )
                    )
                )
                .border(6.dp, Color(0xFF4A2B18), CircleShape)
                .clickable { onBowlClick() },
            contentAlignment = Alignment.Center
        ) {
            // Inner vibrant green felt table
            Box(
                modifier = Modifier
                    .size(270.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF196753),
                                Color(0xFF0F3D32),
                                Color(0xFF092820)
                            )
                        )
                    )
                    .border(2.dp, Color(0xFF235548), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Watermark text
                Text(
                    text = "SICBO\n6 DICE",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF134C3E).copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                // Dice arrangement: 3 Top Row, 3 Bottom Row
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val d = period?.dice ?: listOf(1, 5, 6, 2, 6, 2)

                    // Top Row (Dice 1, 2, 3 - White dice)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SicboDie(value = d[0], size = 46.dp, isRedDie = false)
                        SicboDie(value = d[1], size = 46.dp, isRedDie = false)
                        SicboDie(value = d[2], size = 46.dp, isRedDie = false)
                    }

                    // Countdown Banner: NEXT GAME ON 11
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isShaking) "Mengocok Dadu..." else "NEXT GAME ON $countdown",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF22C55E),
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Bottom Row (Dice 4, 5, 6 - Red tinted dice)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SicboDie(value = d[3], size = 46.dp, isRedDie = true)
                        SicboDie(value = d[4], size = 46.dp, isRedDie = true)
                        SicboDie(value = d[5], size = 46.dp, isRedDie = true)
                    }
                }
            }
        }

        // Floating Action Buttons on the Right (History, Video, Audio)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF381F10))
                    .border(1.dp, Color(0xFF5A3119), CircleShape)
            ) {
                Icon(Icons.Default.History, contentDescription = "History", tint = GoldLight, modifier = Modifier.size(18.dp))
            }

            IconButton(
                onClick = { /* video stream */ },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF381F10))
                    .border(1.dp, Color(0xFF5A3119), CircleShape)
            ) {
                Icon(Icons.Default.PlayCircle, contentDescription = "Stream", tint = GoldLight, modifier = Modifier.size(18.dp))
            }

            IconButton(
                onClick = onToggleAudio,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF381F10))
                    .border(1.dp, Color(0xFF5A3119), CircleShape)
            ) {
                Icon(
                    imageVector = if (isAudioOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Sound",
                    tint = GoldLight,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Result metrics text underneath bowl matching screenshot:
 * BS: BIG  4D BS: TIE  OE: EVEN  4D OE: EVEN
 * SAME 4D: 0  SUM 4D: 14  GROUP: SUM 18-23  TOTAL: 22
 */
@Composable
fun ResultMetricsBar(period: SicboPeriod) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Line 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MetricText(label = "BS:", value = if (period.isBig) "BIG" else if (period.isSmall) "SMALL" else "TIE")
            MetricText(label = "4D BS:", value = period.fourDBS)
            MetricText(label = "OE:", value = if (period.isEven) "EVEN" else "ODD")
            MetricText(label = "4D OE:", value = period.fourDOE)
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Line 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MetricText(label = "SAME 4D:", value = "${period.same4Dice ?: 0}")
            MetricText(label = "SUM 4D:", value = "${period.sum4D}")
            MetricText(label = "GROUP:", value = period.sumGroup)
            MetricText(label = "TOTAL:", value = "${period.total}")
        }
    }
}

@Composable
fun MetricText(label: String, value: String) {
    Row {
        Text(text = "$label ", fontSize = 11.sp, fontWeight = FontWeight.Normal, color = TextMuted)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary)
    }
}

/**
 * The full betting grid matching the screenshot.
 */
@Composable
fun BettingBoardGrid(
    latestPeriod: SicboPeriod?,
    placedBets: Map<String, String>,
    random3DSequence: List<Int>,
    onRefreshRandom3D: () -> Unit,
    onPlaceBet: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: Primary Bets (ODD | BIG | SMALL | EVEN)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val isBigWin = latestPeriod?.isBig == true
            val isSmallWin = latestPeriod?.isSmall == true
            val isEvenWin = latestPeriod?.isEven == true
            val isOddWin = latestPeriod?.isOdd == true

            BetCell(
                title = "ODD",
                subtitle = "Odd Number\n1.985X",
                isWon = isOddWin,
                chip = placedBets["ODD"],
                onClick = { onPlaceBet("ODD") },
                modifier = Modifier.weight(1f)
            )

            BetCell(
                title = "BIG",
                subtitle = "22 - 36\n1.985X",
                isWon = isBigWin,
                chip = placedBets["BIG"],
                onClick = { onPlaceBet("BIG") },
                modifier = Modifier.weight(1f)
            )

            BetCell(
                title = "SMALL",
                subtitle = "6 - 20\n1.985X",
                isWon = isSmallWin,
                chip = placedBets["SMALL"],
                onClick = { onPlaceBet("SMALL") },
                modifier = Modifier.weight(1f)
            )

            BetCell(
                title = "EVEN",
                subtitle = "Even Number\n1.985X",
                isWon = isEvenWin,
                chip = placedBets["EVEN"],
                onClick = { onPlaceBet("EVEN") },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 4D Bets (4D ODD | 4D BIG | 4D SMALL | 4D EVEN)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val is4DBigWin = latestPeriod?.fourDBS == "BIG"
            val is4DSmallWin = latestPeriod?.fourDBS == "SMALL"
            val is4DEvenWin = latestPeriod?.fourDOE == "EVEN"
            val is4DOddWin = latestPeriod?.fourDOE == "ODD"

            BetCell(
                title = "4D ODD",
                subtitle = "Odd Number\n1.985X",
                isWon = is4DOddWin,
                chip = placedBets["4D_ODD"],
                onClick = { onPlaceBet("4D_ODD") },
                modifier = Modifier.weight(1f)
            )

            BetCell(
                title = "4D BIG",
                subtitle = "4 Dice in (4,5,6)\n4X",
                isWon = is4DBigWin,
                chip = placedBets["4D_BIG"],
                onClick = { onPlaceBet("4D_BIG") },
                modifier = Modifier.weight(1f)
            )

            BetCell(
                title = "4D SMALL",
                subtitle = "4 Dice in (1,2,3)\n4X",
                isWon = is4DSmallWin,
                chip = placedBets["4D_SMALL"],
                onClick = { onPlaceBet("4D_SMALL") },
                modifier = Modifier.weight(1f)
            )

            BetCell(
                title = "4D EVEN",
                subtitle = "Even Number\n1.985X",
                isWon = is4DEvenWin,
                chip = placedBets["4D_EVEN"],
                onClick = { onPlaceBet("4D_EVEN") },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Random 3D Bet Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BetButtonTeal)
                .border(1.dp, BetButtonTealBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = BetButtonTealText, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))

            // Random 3D pill: 3 - 5 - 1 with yellow refresh button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0D2520))
                    .border(1.dp, Color(0xFF1B4F43), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${random3DSequence[0]} - ${random3DSequence[1]} - ${random3DSequence[2]}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = GoldLight,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onRefreshRandom3D() }
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "PLACE BET AMOUNT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BetButtonTealText
            )
        }

        // Row 4: SUM Bets (SUM 6-11 | SUM 12-17 | SUM 18-23 | SUM 24-29 | SUM 30-36)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val total = latestPeriod?.total ?: 0
            SumBetCell("SUM 6-11", "86X", isWon = total in 6..11, chip = placedBets["SUM_6_11"], onClick = { onPlaceBet("SUM_6_11") }, modifier = Modifier.weight(1f))
            SumBetCell("SUM 12-17", "4.95X", isWon = total in 12..17, chip = placedBets["SUM_12_17"], onClick = { onPlaceBet("SUM_12_17") }, modifier = Modifier.weight(1f))
            SumBetCell("SUM 18-23", "1.92X", isWon = total in 18..23, chip = placedBets["SUM_18_23"], onClick = { onPlaceBet("SUM_18_23") }, modifier = Modifier.weight(1f))
            SumBetCell("SUM 24-29", "3.75X", isWon = total in 24..29, chip = placedBets["SUM_24_29"], onClick = { onPlaceBet("SUM_24_29") }, modifier = Modifier.weight(1f))
            SumBetCell("SUM 30-36", "45X", isWon = total in 30..36, chip = placedBets["SUM_30_36"], onClick = { onPlaceBet("SUM_30_36") }, modifier = Modifier.weight(1f))
        }

        // Row 5 & 6: 4 SAME DICE (116X)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val sameVal = latestPeriod?.same4Dice
            QuadBetCell("4 DICE 1", "116X", diceFace = 1, isWon = sameVal == 1, chip = placedBets["4D_1"], onClick = { onPlaceBet("4D_1") }, modifier = Modifier.weight(1f))
            QuadBetCell("4 DICE 2", "116X", diceFace = 2, isWon = sameVal == 2, chip = placedBets["4D_2"], onClick = { onPlaceBet("4D_2") }, modifier = Modifier.weight(1f))
            QuadBetCell("4 DICE 3", "116X", diceFace = 3, isWon = sameVal == 3, chip = placedBets["4D_3"], onClick = { onPlaceBet("4D_3") }, modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val sameVal = latestPeriod?.same4Dice
            QuadBetCell("4 DICE 4", "116X", diceFace = 4, isWon = sameVal == 4, chip = placedBets["4D_4"], onClick = { onPlaceBet("4D_4") }, modifier = Modifier.weight(1f))
            QuadBetCell("4 DICE 5", "116X", diceFace = 5, isWon = sameVal == 5, chip = placedBets["4D_5"], onClick = { onPlaceBet("4D_5") }, modifier = Modifier.weight(1f))
            QuadBetCell("4 DICE 6", "116X", diceFace = 6, isWon = sameVal == 6, chip = placedBets["4D_6"], onClick = { onPlaceBet("4D_6") }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun BetCell(
    title: String,
    subtitle: String,
    isWon: Boolean,
    chip: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isWon) BetButtonWinLime else BetButtonTeal
    val textColor = if (isWon) BetButtonWinText else BetButtonTealText
    val borderColor = if (isWon) Color(0xFF65A30D) else BetButtonTealBorder

    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )
            Text(
                text = subtitle,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 10.sp
            )
        }

        // Chip badge if bet placed
        if (chip != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary)
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✓", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun SumBetCell(
    title: String,
    multiplier: String,
    isWon: Boolean,
    chip: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isWon) BetButtonWinLime else BetButtonTeal
    val textColor = if (isWon) BetButtonWinText else BetButtonTealText
    val borderColor = if (isWon) Color(0xFF65A30D) else BetButtonTealBorder

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(text = multiplier, fontSize = 9.sp, fontWeight = FontWeight.Black, color = textColor)
        }
    }
}

@Composable
fun QuadBetCell(
    title: String,
    multiplier: String,
    diceFace: Int,
    isWon: Boolean,
    chip: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isWon) BetButtonWinLime else BetButtonTeal
    val textColor = if (isWon) BetButtonWinText else BetButtonTealText

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, if (isWon) Color(0xFF65A30D) else BetButtonTealBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Black, color = textColor)
                Text(text = multiplier, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textColor)
            }
            // 4-Dice Icon graphic representation
            MiniDiceIcon(face = diceFace, isWon = isWon)
        }
    }
}

@Composable
fun MiniDiceIcon(face: Int, isWon: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isWon) Color(0xFF14532D) else Color(0xFF0F2620))
            .border(0.5.dp, Color(0xFF266255), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "⚅",
            fontSize = 18.sp,
            color = if (isWon) BetButtonWinLime else BetButtonTealText
        )
    }
}

/**
 * Chips selector row with authentic striped poker chips.
 */
@Composable
fun ChipsSelectorRow(
    selectedChip: String,
    onSelectChip: (String) -> Unit
) {
    val chips = listOf(
        Triple("500", Color(0xFF475569), Color.White),
        Triple("1K", Color(0xFFDB2777), Color.White),
        Triple("5K", Color(0xFF2563EB), Color.White),
        Triple("10K", Color(0xFFDC2626), Color.White),
        Triple("50K", Color(0xFFCA8A04), Color.Black),
        Triple("100K", Color(0xFF16A34A), Color.White),
        Triple("250K", Color(0xFF7C3AED), Color.White),
        Triple("1M", Color(0xFF0284C7), Color.White),
        Triple("2M", Color(0xFFB45309), Color.White)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        chips.forEach { (label, bgCol, textCol) ->
            val isSelected = selectedChip == label
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(bgCol)
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) Color.White else Color(0xFFCBD5E1).copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable { onSelectChip(label) }
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                // Inner dashed circle pattern
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(bgCol)
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = textCol
                    )
                }
            }
        }
    }
}

/**
 * Bottom action buttons: UNDO, CLEAR, REBET, SUBMIT matching screenshot.
 */
@Composable
fun ActionButtonsRow(
    onUndo: () -> Unit,
    onClear: () -> Unit,
    onRebet: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        GameActionButton(text = "UNDO", color = ActionUndo, onClick = onUndo, modifier = Modifier.weight(1f))
        GameActionButton(text = "CLEAR", color = ActionClear, onClick = onClear, modifier = Modifier.weight(1f))
        GameActionButton(text = "REBET", color = ActionRebet, onClick = onRebet, modifier = Modifier.weight(1f))
        GameActionButton(text = "SUBMIT", color = ActionSubmit, onClick = onSubmit, modifier = Modifier.weight(1f))
    }
}

@Composable
fun GameActionButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(color)
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(19.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}
