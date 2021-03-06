package kr.jadekim.terra.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.model.CoinDecimal
import kr.jadekim.terra.type.ULongAsStringSerializer

@Serializable
data class BaseRequest(
    @SerialName("from") val fromAddress: String,
    val memo: String,
    @SerialName("chain_id") val chainId: String,
    @SerialName("account_number") @Serializable(ULongAsStringSerializer::class) val accountNumber: ULong,
    @Serializable(ULongAsStringSerializer::class) val sequence: ULong,
    @SerialName("timeout_height") @Serializable(ULongAsStringSerializer::class) val timeoutHeight: ULong? = null,
    val fees: List<Coin>? = null,
    @SerialName("gas_prices") val gasPrices: List<CoinDecimal>? = null,
    val gas: String? = null,
    @SerialName("gas_adjustment") val gasAdjustment: String? = null,
    val simulate: Boolean = false,
)
