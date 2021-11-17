package kr.jadekim.terra.client.transaction

import kr.jadekim.terra.client.api.TransactionApi
import kr.jadekim.terra.model.CoinDecimal
import kr.jadekim.terra.model.Fee
import kr.jadekim.terra.model.Message
import kr.jadekim.terra.transaction.provider.GasPricesProvider
import kr.jadekim.terra.transaction.tool.EstimateFeeException
import kr.jadekim.terra.transaction.tool.FeeEstimator

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