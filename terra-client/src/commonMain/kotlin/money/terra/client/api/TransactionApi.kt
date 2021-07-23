package money.terra.client.api

import kotlinx.coroutines.Deferred
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import money.terra.client.model.Result
import money.terra.model.*
import money.terra.transaction.broadcaster.BroadcastResult
import money.terra.type.ULongAsStringSerializer

interface TransactionApi {

    fun getByHash(transactionHash: String): Deferred<TransactionResult?>

    fun broadcastAsync(transaction: Transaction): Deferred<BroadcastAsyncResult>

    fun broadcastSync(transaction: Transaction): Deferred<BroadcastSyncResult>

    fun broadcastBlock(transaction: Transaction): Deferred<BroadcastBlockResult>

    fun estimateFee(
        transaction: Transaction,
        gasAdjustment: Float,
        gasPrices: List<CoinDecimal>,
    ): Deferred<Result<EstimateFeeResult>>
}

interface BroadcastRequest {
    val transaction: Transaction
    val mode: String
}

@Serializable
class BroadcastSyncRequest(
    @SerialName("tx") override val transaction: Transaction,
) : BroadcastRequest {
    override val mode = "sync"
}

@Serializable
class BroadcastAsyncRequest(
    @SerialName("tx") override val transaction: Transaction,
) : BroadcastRequest {
    override val mode = "async"
}

@Serializable
class BroadcastBlockRequest(
    @SerialName("tx") override val transaction: Transaction,
) : BroadcastRequest {
    override val mode = "block"
}

@Serializable
data class BroadcastAsyncResult(
    @Serializable(ULongAsStringSerializer::class) override val height: ULong,
    @SerialName("txhash") override val transactionHash: String,
    override val code: Int? = null,
) : BroadcastResult

@Serializable
data class BroadcastSyncResult(
    @Serializable(ULongAsStringSerializer::class) override val height: ULong,
    @SerialName("txhash") override val transactionHash: String,
    override val code: Int? = null,
    @SerialName("raw_log") val rawLog: String? = null,
) : BroadcastResult

@Serializable
data class BroadcastBlockResult(
    @Serializable(ULongAsStringSerializer::class) override val height: ULong,
    @SerialName("txhash") override val transactionHash: String,
    override val code: Int? = null,
    @SerialName("raw_log") val rawLog: String? = null,
    val logs: List<TransactionLog>? = null,
    val gasUsed: ULong? = null,
    val gasWanted: ULong? = null,
    val events: List<TransactionEvent>? = null,
) : BroadcastResult

@Serializable
data class EstimateFeeRequest(
    @SerialName("tx") val transaction: Transaction,
    @SerialName("gas_adjustment") val gasAdjustment: String,
    @SerialName("gas_prices") val gasPrices: List<CoinDecimal>,
)

@Serializable
data class EstimateFeeResult(
    val fees: List<Coin>,
    @Serializable(ULongAsStringSerializer::class) val gas: ULong,
) {

    fun asFee() = Fee(gas, fees)
}