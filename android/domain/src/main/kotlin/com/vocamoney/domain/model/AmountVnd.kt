package com.vocamoney.domain.model

/**
 * A positive money amount in Vietnamese dong. VND has no minor unit in practice,
 * so the value is whole dong. Never use Float/Double for money (PRD §0).
 */
@JvmInline
value class AmountVnd private constructor(val dong: Long) {

    override fun toString(): String = format()

    /** Formats as Vietnamese copy, e.g. 35000 -> "35.000 ₫". */
    fun format(): String {
        val digits = dong.toString()
        val grouped = digits.reversed().chunked(3).joinToString(".").reversed()
        return "$grouped ₫"
    }

    companion object {
        /** Upper safeguard: 100 billion dong. Larger values need explicit review (BR-13). */
        const val MAX_DONG: Long = 100_000_000_000L

        /** Returns null for zero, negative or out-of-range values instead of guessing (BR-02, BR-13). */
        fun of(dong: Long): AmountVnd? = if (dong in 1..MAX_DONG) AmountVnd(dong) else null
    }
}
