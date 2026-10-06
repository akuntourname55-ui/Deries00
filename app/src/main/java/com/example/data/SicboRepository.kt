package com.example.data

import com.example.model.GameStatistics
import com.example.model.PatternStats
import com.example.model.PredictionInfo
import com.example.model.SicboPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class SicboRepository {

    private val _periods = MutableStateFlow<List<SicboPeriod>>(emptyList())
    val periods: StateFlow<List<SicboPeriod>> = _periods.asStateFlow()

    private var currentPeriodSequence = 125

    init {
        generateSeedData()
    }

    private fun generateSeedData() {
        val initialList = mutableListOf<SicboPeriod>()
        val baseTime = System.currentTimeMillis() - (75 * 60 * 1000L)
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val datePrefix = dateFormat.format(Date())

        // Generate 75 realistic rounds
        for (i in 1..75) {
            val periodNum = "$datePrefix-${String.format(Locale.US, "%03d", i)}"
            val time = baseTime + (i * 60 * 1000L)

            // 6 dice roll
            val dice = List(6) { Random.nextInt(1, 7) }
            initialList.add(SicboPeriod(periodNum, time, dice))
        }

        currentPeriodSequence = 76
        _periods.value = initialList.reversed()
    }

    fun rollNewPeriod(): SicboPeriod {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val datePrefix = dateFormat.format(Date())
        val periodNum = "$datePrefix-${String.format(Locale.US, "%03d", currentPeriodSequence++)}"
        val dice = List(6) { Random.nextInt(1, 7) }
        val newPeriod = SicboPeriod(
            periodNumber = periodNum,
            timestamp = System.currentTimeMillis(),
            dice = dice
        )

        val current = _periods.value.toMutableList()
        current.add(0, newPeriod)
        _periods.value = current
        return newPeriod
    }

    fun addCustomPeriod(dice: List<Int>, customPeriodNumber: String? = null): SicboPeriod {
        require(dice.size == 6) { "Dadu harus berjumlah tepat 6" }
        require(dice.all { it in 1..6 }) { "Nilai setiap dadu harus antara 1 dan 6" }

        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val datePrefix = dateFormat.format(Date())
        val periodNum = if (!customPeriodNumber.isNullOrBlank()) {
            customPeriodNumber.trim()
        } else {
            "$datePrefix-${String.format(Locale.US, "%03d", currentPeriodSequence++)}"
        }

        val period = SicboPeriod(
            periodNumber = periodNum,
            timestamp = System.currentTimeMillis(),
            dice = dice
        )

        val current = _periods.value.toMutableList()
        current.add(0, period)
        _periods.value = current
        return period
    }

    fun resetToSeed() {
        generateSeedData()
    }

    fun computeStats(history: List<SicboPeriod>): PatternStats {
        if (history.isEmpty()) {
            val emptyGameStats = GameStatistics(
                smallPct = 43, tiePct = 13, bigPct = 44,
                fourDSmallPct = 21, fourDTiePct = 59, fourDBigPct = 20,
                oddPct = 52, evenPct = 48,
                fourDOddPct = 53, fourDEvenPct = 47
            )
            return PatternStats(
                totalRounds = 0,
                bigCount = 0, smallCount = 0, tieCount = 0,
                evenCount = 0, oddCount = 0,
                bigPercent = 0f, smallPercent = 0f,
                evenPercent = 0f, oddPercent = 0f,
                currentStreakType = "-",
                currentStreakCount = 0,
                longestBigStreak = 0,
                longestSmallStreak = 0,
                longestEvenStreak = 0,
                longestOddStreak = 0,
                diceCounts = (1..6).associateWith { 0 },
                positionAverages = List(6) { 0f },
                hotDice = 1,
                coldDice = 6,
                averageTotal = 0f,
                gameStats = emptyGameStats,
                prediction = PredictionInfo(
                    bsRecommendation = "Menunggu Data",
                    bsConfidence = 50,
                    bsReason = "Belum ada data pengeluaran.",
                    oeRecommendation = "Menunggu Data",
                    oeConfidence = 50,
                    oeReason = "Belum ada data pengeluaran.",
                    suggestedBetType = "-"
                )
            )
        }

        val totalRounds = history.size
        val bigCount = history.count { it.isBig }
        val smallCount = history.count { it.isSmall }
        val tieCount = history.count { it.isTie }
        val evenCount = history.count { it.isEven }
        val oddCount = history.count { it.isOdd }

        val bigPercent = (bigCount.toFloat() / totalRounds) * 100f
        val smallPercent = (smallCount.toFloat() / totalRounds) * 100f
        val evenPercent = (evenCount.toFloat() / totalRounds) * 100f
        val oddPercent = (oddCount.toFloat() / totalRounds) * 100f

        val fourDSmall = history.count { it.fourDBS == "SMALL" }
        val fourDTie = history.count { it.fourDBS == "TIE" }
        val fourDBig = history.count { it.fourDBS == "BIG" }

        val fourDOdd = history.count { it.fourDOE == "ODD" }
        val fourDEven = history.count { it.fourDOE == "EVEN" }

        val gameStats = GameStatistics(
            smallPct = ((smallCount.toFloat() / totalRounds) * 100).toInt(),
            tiePct = ((tieCount.toFloat() / totalRounds) * 100).toInt(),
            bigPct = ((bigCount.toFloat() / totalRounds) * 100).toInt(),
            fourDSmallPct = ((fourDSmall.toFloat() / totalRounds) * 100).toInt(),
            fourDTiePct = ((fourDTie.toFloat() / totalRounds) * 100).toInt(),
            fourDBigPct = ((fourDBig.toFloat() / totalRounds) * 100).toInt(),
            oddPct = ((oddCount.toFloat() / totalRounds) * 100).toInt(),
            evenPct = ((evenCount.toFloat() / totalRounds) * 100).toInt(),
            fourDOddPct = ((fourDOdd.toFloat() / totalRounds) * 100).toInt(),
            fourDEvenPct = ((fourDEven.toFloat() / totalRounds) * 100).toInt()
        )

        val avgTotal = history.map { it.total }.average().toFloat()

        // Calculate streaks (chronological: oldest to newest)
        val chrono = history.reversed()
        var curBigStreak = 0; var maxBigStreak = 0
        var curSmallStreak = 0; var maxSmallStreak = 0
        var curEvenStreak = 0; var maxEvenStreak = 0
        var curOddStreak = 0; var maxOddStreak = 0

        for (p in chrono) {
            if (p.isBig) {
                curBigStreak++; curSmallStreak = 0
                if (curBigStreak > maxBigStreak) maxBigStreak = curBigStreak
            } else if (p.isSmall) {
                curSmallStreak++; curBigStreak = 0
                if (curSmallStreak > maxSmallStreak) maxSmallStreak = curSmallStreak
            } else {
                curBigStreak = 0; curSmallStreak = 0
            }

            if (p.isEven) {
                curEvenStreak++; curOddStreak = 0
                if (curEvenStreak > maxEvenStreak) maxEvenStreak = curEvenStreak
            } else {
                curOddStreak++; curEvenStreak = 0
                if (curOddStreak > maxOddStreak) maxOddStreak = curOddStreak
            }
        }

        // Active streak at newest period
        val latest = history.first()
        val activeStreakType = when {
            latest.isBig -> "BIG"
            latest.isSmall -> "SMALL"
            else -> "TIE"
        }
        var activeStreakCount = 0
        for (p in history) {
            if ((latest.isBig && p.isBig) || (latest.isSmall && p.isSmall)) {
                activeStreakCount++
            } else {
                break
            }
        }

        // Dice frequency
        val diceCounts = mutableMapOf<Int, Int>().apply { for (i in 1..6) put(i, 0) }
        val posSums = MutableList(6) { 0 }
        for (p in history) {
            p.dice.forEachIndexed { idx, d ->
                diceCounts[d] = (diceCounts[d] ?: 0) + 1
                posSums[idx] += d
            }
        }

        val positionAverages = posSums.map { it.toFloat() / totalRounds }
        val hotDice = diceCounts.maxByOrNull { it.value }?.key ?: 1
        val coldDice = diceCounts.minByOrNull { it.value }?.key ?: 6

        val prediction = analyzePattern(history, activeStreakType, activeStreakCount, bigPercent, evenPercent)

        return PatternStats(
            totalRounds = totalRounds,
            bigCount = bigCount,
            smallCount = smallCount,
            tieCount = tieCount,
            evenCount = evenCount,
            oddCount = oddCount,
            bigPercent = bigPercent,
            smallPercent = smallPercent,
            evenPercent = evenPercent,
            oddPercent = oddPercent,
            currentStreakType = activeStreakType,
            currentStreakCount = activeStreakCount,
            longestBigStreak = maxBigStreak,
            longestSmallStreak = maxSmallStreak,
            longestEvenStreak = maxEvenStreak,
            longestOddStreak = maxOddStreak,
            diceCounts = diceCounts,
            positionAverages = positionAverages,
            hotDice = hotDice,
            coldDice = coldDice,
            averageTotal = avgTotal,
            gameStats = gameStats,
            prediction = prediction
        )
    }

    private fun analyzePattern(
        history: List<SicboPeriod>,
        streakType: String,
        streakCount: Int,
        bigPercent: Float,
        evenPercent: Float
    ): PredictionInfo {
        val recent10 = history.take(10)
        val recentBig = recent10.count { it.isBig }
        val recentEven = recent10.count { it.isEven }

        val (bsRec, bsConf, bsReason) = when {
            streakCount >= 4 -> {
                val opposite = if (streakType == "BIG") "SMALL" else "BIG"
                Triple(
                    opposite,
                    74,
                    "Pola Naga $streakType berlangsung $streakCount periode. Peluang pembalikan menuju $opposite tinggi."
                )
            }
            streakCount in 2..3 -> {
                Triple(
                    streakType,
                    68,
                    "Tren $streakType aktif ($streakCount putaran berurutan). Mengikuti alur naga disarankan."
                )
            }
            recentBig >= 7 -> {
                Triple("SMALL", 65, "BIG keluar ${recentBig * 10}% dalam 10 putaran terakhir. Peluang SMALL meningkat.")
            }
            recentBig <= 3 -> {
                Triple("BIG", 66, "SMALL dominan baru-baru ini. Indikasi kenaikan menuju BIG.")
            }
            else -> {
                val favoured = if (bigPercent < 50f) "BIG" else "SMALL"
                Triple(favoured, 58, "Pola seimbang. Regresi 6 dadu condong ke $favoured.")
            }
        }

        val (oeRec, oeConf, oeReason) = when {
            recentEven >= 7 -> Triple("ODD", 67, "EVEN keluar $recentEven kali dalam 10 putaran. Peluang ODD meningkat.")
            recentEven <= 3 -> Triple("EVEN", 68, "ODD dominan baru-baru ini. Reversi ke EVEN diperkirakan.")
            else -> {
                val choice = if (history.first().isEven) "ODD" else "EVEN"
                Triple(choice, 60, "Tren Paritas bergantian. Disarankan pasang $choice.")
            }
        }

        return PredictionInfo(
            bsRecommendation = bsRec,
            bsConfidence = bsConf,
            bsReason = bsReason,
            oeRecommendation = oeRec,
            oeConfidence = oeConf,
            oeReason = oeReason,
            suggestedBetType = "$bsRec + $oeRec"
        )
    }
}
