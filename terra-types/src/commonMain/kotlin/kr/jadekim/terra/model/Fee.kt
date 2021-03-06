package kr.jadekim.terra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.jadekim.terra.type.ULongAsStringSerializer

@Serializable
data class Fee(
    @SerialName("gas") @Serializable(ULongAsStringSerializer::class) val gasAmount: ULong,
    @SerialName("amount") val feeAmount: List<Coin>,
)
