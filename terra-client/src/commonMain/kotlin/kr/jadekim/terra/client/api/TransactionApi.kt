package kr.jadekim.terra.client.api

import kotlinx.coroutines.Deferred
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.jadekim.terra.client.model.BaseRequest
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.model.*
import kr.jadekim.terra.transaction.broadcaster.BroadcastResult
import kr.jadekim.terra.type.ULongAsStringSerializer

interface TransactionApi {

    fun getByHash(transactionHash: String): Deferred<TransactionResult?>

    fun broadcastAsync(transaction: Transaction): Deferred<BroadcastAsyncResult>

    fun broadcastSync(transaction: Transaction): Deferred<BroadcastSyncResult>

    fun broadcastBlock(transaction: Transaction): Deferred<BroadcastBlockResult>

    fun estimateFee(
        messages: List<Message>,
        senderAddress: String,
        senderAccountNumber: ULong,
        senderSequence: ULong,
        gasPrices: List<CoinDecimal>,
        gasAdjustment: Float,
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
    @SerialName("txhash") override val transactionHash: String,
    @SerialName("codespace") override val codeSpace: String? = null,
    override val code: Int? = null,
    @SerialName("raw_log") override val rawLog: String? = null,
    override val logs: List<TransactionLog>? = null,
) : BroadcastResult

@Serializable
data class BroadcastSyncResult(
    @SerialName("txhash") override val transactionHash: String,
    @SerialName("codespace") override val codeSpace: String? = null,
    override val code: Int? = null,
    @SerialName("raw_log") override val rawLog: String? = null,
    override val logs: List<TransactionLog>? = null,
) : BroadcastResult

@Serializable
data class BroadcastBlockResult(
    @Serializable(ULongAsStringSerializer::class) val height: ULong,
    @SerialName("txhash") override val transactionHash: String,
    @SerialName("codespace") override val codeSpace: String? = null,
    override val code: Int? = null,
    @SerialName("raw_log") override val rawLog: String? = null,
    override val logs: List<TransactionLog>? = null,
    val gasUsed: ULong? = null,
    val gasWanted: ULong? = null,
) : BroadcastResult

@Serializable
data class EstimateFeeRequest(
    @SerialName("base_req") val baseRequest: BaseRequest,
    @SerialName("msgs") val messages: List<Message>,
) {

    constructor(
        messages: List<Message>,
        chainId: String,
        requester: String,
        requesterAccountNumber: ULong,
        requesterSequence: ULong,
        gasPrices: List<CoinDecimal>,
        gasAdjustment: Float,
    ) : this(
        BaseRequest(
            fromAddress = requester,
            memo = "",
            chainId = chainId,
            accountNumber = requesterAccountNumber,
            sequence = requesterSequence,
            gasPrices = gasPrices,
            gasAdjustment = gasAdjustment.toString(),
            gas = "auto",
        ),
        messages,
    )
}

@Serializable
data class EstimateFeeResult(
    val fee: Fee,
)