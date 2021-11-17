package kr.jadekim.terra.transaction.tool

import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.model.CoinDecimal
import kr.jadekim.terra.model.Fee
import kr.jadekim.terra.model.Message
import kr.jadekim.terra.transaction.provider.GasPricesProvider
import kr.jadekim.terra.transaction.provider.StaticGasPricesProvider
import kr.jadekim.terra.type.Decimal
import kr.jadekim.terra.type.times
import kr.jadekim.terra.type.toDecimal
import kr.jadekim.terra.type.toUint128

class EstimateFeeException(
    val messages: List<Message>,
    val requesterAddress: String,
    val requesterAccountNumber: ULong,
    val requesterSequence: ULong,
    val gasPrices: List<CoinDecimal>,
    val gasAdjustment: Float,
    val reason: String,
    cause: Throwable? = null,
) : Exception(reason, cause)

abstract class FeeEstimator {

    val gasPricesProvider: GasPricesProvider

    var defaultGasAdjustment: Float = 1.4f
    var defaultFeeDenomination: String = "uluna"

    constructor(gasPrices: Map<String, Decimal>) {
        gasPricesProvider = StaticGasPricesProvider(gasPrices)
    }

    constructor(gasPricesProvider: GasPricesProvider) {
        this.gasPricesProvider = gasPricesProvider
    }

    @Throws(EstimateFeeException::class)
    suspend fun estimate(
        messages: List<Message>,
        senderAddress: String,
        senderAccountNumber: ULong,
        senderSequence: ULong,
        feeDenomination: String = defaultFeeDenomination,
        gasAdjustment: Float = defaultGasAdjustment,
    ): Fee {
        val gasPrice = gasPricesProvider.get(feeDenomination)

        return estimate(
            messages,
            senderAddress,
            senderAccountNumber,
            senderSequence,
            listOf(CoinDecimal(feeDenomination, gasPrice)),
            gasAdjustment,
        )
    }

    suspend fun estimate(gasAmount: ULong, feeDenomination: String = defaultFeeDenomination): Fee {
        val gasPrice = gasPricesProvider.get(feeDenomination)
        val feeAmount = (gasAmount.toDecimal() * gasPrice).toUint128()

        return Fee(gasAmount, listOf(Coin(feeDenomination, feeAmount)))
    }

    @Throws(EstimateFeeException::class)
    protected abstract suspend fun estimate(
        messages: List<Message>,
        senderAddress: String,
        senderAccountNumber: ULong,
        senderSequence: ULong,
        gasPrices: List<CoinDecimal>,
        gasAdjustment: Float,
    ): Fee
}
