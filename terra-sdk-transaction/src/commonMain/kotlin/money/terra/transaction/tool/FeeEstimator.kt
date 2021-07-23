package money.terra.transaction.tool

import kotlinx.coroutines.*
import money.terra.model.Coin
import money.terra.model.CoinDecimal
import money.terra.model.Fee
import money.terra.model.Transaction
import money.terra.transaction.provider.GasPricesProvider
import money.terra.transaction.provider.StaticGasPricesProvider
import money.terra.type.Decimal
import money.terra.type.times
import money.terra.type.toDecimal
import money.terra.type.toUint128

class EstimateFeeException(
    val transaction: Transaction,
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
        transaction: Transaction,
        feeDenomination: String = defaultFeeDenomination,
        gasAdjustment: Float = defaultGasAdjustment,
    ): Fee {
        val gasPrice = gasPricesProvider.get(feeDenomination)

        return estimate(transaction, listOf(CoinDecimal(feeDenomination, gasPrice)), gasAdjustment)
    }

    suspend fun estimate(gasAmount: ULong, feeDenomination: String = defaultFeeDenomination): Fee {
        val gasPrice = gasPricesProvider.get(feeDenomination)
        val feeAmount = (gasAmount.toDecimal() * gasPrice).toUint128()

        return Fee(gasAmount, listOf(Coin(feeDenomination, feeAmount)))
    }

    @Throws(EstimateFeeException::class)
    protected abstract suspend fun estimate(
        transaction: Transaction,
        gasPrices: List<CoinDecimal>,
        gasAdjustment: Float,
    ): Fee
}

@Throws(EstimateFeeException::class)
fun Transaction.estimateFee(
    feeEstimator: FeeEstimator,
    gasAmount: ULong? = null,
    feeDenomination: String? = null,
    gasAdjustment: Float? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Deferred<Transaction> = CoroutineScope(dispatcher).async {
    val fee = if (gasAmount == null) {
        feeEstimator.estimate(
            this@estimateFee,
            feeDenomination ?: feeEstimator.defaultFeeDenomination,
            gasAdjustment ?: feeEstimator.defaultGasAdjustment,
        )
    } else {
        feeEstimator.estimate(gasAmount, feeDenomination ?: feeEstimator.defaultFeeDenomination)
    }

    this@estimateFee.copy(fee = fee, signatures = null)
}
