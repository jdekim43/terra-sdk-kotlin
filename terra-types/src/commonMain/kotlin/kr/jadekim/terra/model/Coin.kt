package kr.jadekim.terra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.jadekim.terra.type.Decimal
import kr.jadekim.terra.type.Uint128

@Serializable
data class Coin(
    val amount: Uint128,
    @SerialName("denom") val denomination: String,
) {

    constructor(denomination: String, amount: Uint128) : this(amount, denomination)

    override fun toString(): String = amount.toString() + denomination
}

@Serializable
data class CoinDecimal(
    val amount: Decimal,
    @SerialName("denom") val denomination: String,
) {

    constructor(denomination: String, amount: Decimal) : this(amount, denomination)

    override fun toString(): String = amount.toString() + denomination
}
