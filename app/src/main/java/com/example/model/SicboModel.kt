package com.example.model

/**
 * Data model for a single Sicbo 6 Dice round/period matching the exact
 * rules of game.yukmainlottery.com.
 */
data class SicboPeriod(
    val periodNumber: String,
    val timestamp: Long,
    val dice: List<Int>, // 6 dice (values 1 to 6)
) {
    val total: Int = dice.sum()

    // In this exact game:
    // SMALL: 6 - 20
    // TIE: 21
    // BIG: 22 - 36
    val isSmall: Boolean = total in 6..20
    val isTie: Boolean = total == 21
    val isBig: Boolean = total in 22..36

    val isEven: Boolean = total % 2 == 0
    val isOdd: Boolean = total % 2 != 0

    // 4D Small / Big:
    // Count dice in {1,2,3} vs {4,5,6}
    val countLow: Int = dice.count { it in 1..3 }
    val countHigh: Int = dice.count { it in 4..6 }
    val fourDBS: String = when {
        countHigh >= 4 -> "BIG"
        countLow >= 4 -> "SMALL"
        else -> "TIE"
    }

    // 4D Odd / Even:
    val countOdd: Int = dice.count { it % 2 != 0 }
    val countEven: Int = dice.count { it % 2 == 0 }
    val fourDOE: String = when {
        countEven > countOdd -> "EVEN"
        countOdd > countEven -> "ODD"
        else -> "EVEN" // or tie
    }

    // Check if 4 or more dice are identical (4 Same Dice)
    val same4Dice: Int? = run {
        val counts = dice.groupingBy { it }.eachCount()
        counts.entries.firstOrNull { it.value >= 4 }?.key
    }

    // Sum Group category
    val sumGroup: String = when (total) {
        in 6..11 -> "SUM 6-11"
        in 12..17 -> "SUM 12-17"
        in 18..23 -> "SUM 18-23"
        in 24..29 -> "SUM 24-29"
        else -> "SUM 30-36"
    }

    val sum4D: Int = dice.take(4).sum()

    // Random 3D groups (front 3 dice and back 3 dice)
    val front3D: List<Int> = dice.take(3)
    val back3D: List<Int> = dice.takeLast(3)
}

/**
 * Filter options for history and analysis.
 */
enum class FilterType(val label: String) {
    ALL("Semua"),
    BIG("BIG (22-36)"),
    SMALL("SMALL (6-20)"),
    TIE("TIE (21)"),
    EVEN("EVEN"),
    ODD("ODD"),
    SAME_4("4 Same Dice")
}

/**
 * Detailed statistical table matching the STATISTIC panel from screenshot:
 * SMALL | TIE | BIG
 * 4D SMALL | 4D TIE | 4D BIG
 * ODD | EVEN
 * 4D ODD | 4D EVEN
 */
data class GameStatistics(
    val smallPct: Int,
    val tiePct: Int,
    val bigPct: Int,
    val fourDSmallPct: Int,
    val fourDTiePct: Int,
    val fourDBigPct: Int,
    val oddPct: Int,
    val evenPct: Int,
    val fourDOddPct: Int,
    val fourDEvenPct: Int,
)

/**
 * Statistical calculations over historical periods.
 */
data class PatternStats(
    val totalRounds: Int,
    val bigCount: Int,
    val smallCount: Int,
    val tieCount: Int,
    val evenCount: Int,
    val oddCount: Int,
    val bigPercent: Float,
    val smallPercent: Float,
    val evenPercent: Float,
    val oddPercent: Float,
    val currentStreakType: String,
    val currentStreakCount: Int,
    val longestBigStreak: Int,
    val longestSmallStreak: Int,
    val longestEvenStreak: Int,
    val longestOddStreak: Int,
    val diceCounts: Map<Int, Int>, // 1..6
    val positionAverages: List<Float>,
    val hotDice: Int,
    val coldDice: Int,
    val averageTotal: Float,
    val gameStats: GameStatistics,
    val prediction: PredictionInfo
)

data class PredictionInfo(
    val bsRecommendation: String,
    val bsConfidence: Int,
    val bsReason: String,
    val oeRecommendation: String,
    val oeConfidence: Int,
    val oeReason: String,
    val suggestedBetType: String
)
