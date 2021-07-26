package kr.jadekim.terra.type

import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable(Uint128Serializer::class)
actual class Uint128 internal constructor(internal val origin: BigInteger) : Number(), Comparable<Uint128> {

    actual constructor(value: String) : this(BigInteger(value))

    override fun compareTo(other: Uint128): Int = origin.compareTo(other.origin)

    override fun toByte(): Byte = origin.toByte()

    override fun toChar(): Char = origin.toChar()

    override fun toDouble(): Double = origin.toDouble()

    override fun toFloat(): Float = origin.toFloat()

    override fun toInt(): Int = origin.toInt()

    override fun toLong(): Long = origin.toLong()

    override fun toShort(): Short = origin.toShort()

    override fun toString(): String = origin.toString()

    override fun equals(other: Any?): Boolean = origin.equals(other)

    override fun hashCode(): Int = origin.hashCode()
}

actual operator fun Uint128.times(other: Uint128): Uint128 = Uint128(origin.multiply(other.origin))