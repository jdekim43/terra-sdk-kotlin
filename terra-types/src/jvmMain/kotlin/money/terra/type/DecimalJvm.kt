package money.terra.type

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

@Serializable(DecimalSerializer::class)
actual class Decimal internal constructor(internal val origin: BigDecimal) : Number(), Comparable<Decimal> {

    actual constructor(value: String) : this(BigDecimal(value))

    override fun compareTo(other: Decimal): Int = origin.compareTo(other.origin)

    override fun toByte(): Byte = origin.toByte()

    override fun toChar(): Char = origin.toChar()

    override fun toDouble(): Double = origin.toDouble()

    override fun toFloat(): Float = origin.toFloat()

    override fun toInt(): Int = origin.toInt()

    override fun toLong(): Long = origin.toLong()

    override fun toShort(): Short = origin.toShort()

    override fun toString(): String = origin.toPlainString()

    override fun equals(other: Any?): Boolean = origin.equals(other)

    override fun hashCode(): Int = origin.hashCode()
}

actual operator fun Decimal.times(other: Decimal): Decimal = Decimal(origin.multiply(other.origin))

actual fun Decimal.ceil(): Decimal = Decimal(origin.setScale(0, RoundingMode.CEILING))

actual fun Decimal.toUint128(): Uint128 = Uint128(origin.toBigInteger())