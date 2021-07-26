package money.terra.client.transaction

import money.terra.client.api.TransactionApi
import money.terra.model.CoinDecimal
import money.terra.model.Fee
import money.terra.model.Message
import money.terra.transaction.provider.GasPricesProvider
import money.terra.transaction.tool.EstimateFeeException
import money.terra.transaction.tool.FeeEstimator

class NodeFeeEstimator(
    private val transactionApi: TransactionApi,
    gasPricesProvider: GasPricesProvider,
) : FeeEstimator(gasPricesProvider) {

    override suspend fun estimate(
        messages: List<Message>,
        senderAddress: String,
        senderAccountNumber: ULong,
        senderSequence: ULong,
        gasPrices: List<CoinDecimal>,
        gasAdjustment: Float,
    ): Fee {
        return try {
            transactionApi.estimateFee(
                messages,
                senderAddress,
                senderAccountNumber,
                senderSequence,
                gasPrices,
                gasAdjustment,
            ).await().result.fee
        } catch (e: Exception) {
            if (e is EstimateFeeException) {
                throw e
            }

            val reason = e.message ?: "Occur error in ${this::class.qualifiedName ?: "NodeFeeEstimator"}"

            throw EstimateFeeException(
                messages,
                senderAddress,
                senderAccountNumber,
                senderSequence,
                gasPrices,
                gasAdjustment,
                reason,
                e,
            )
        }
    }
}