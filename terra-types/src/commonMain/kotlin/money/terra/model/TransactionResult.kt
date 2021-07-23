package money.terra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import money.terra.model.Transaction
import money.terra.model.TransactionLog
import money.terra.model.TypeWrapper
import money.terra.serializer.Base64Serializer
import money.terra.type.ULongAsStringSerializer

@Serializable
data class TransactionResult(
    @Serializable(ULongAsStringSerializer::class) val height: ULong,
    @SerialName("txhash") val transactionHash: String,
    @SerialName("raw_log") val rawLog: String,
    @SerialName("gas_wanted") val gasWanted: ULong,
    @SerialName("gas_used") val gasUsed: ULong,
    @SerialName("tx") val transaction: TypeWrapper<Transaction>,
    val timestamp: String,
    @SerialName("codespace") val codeSpace: String? = null,
    val code: Int? = null,
    @Serializable(Base64Serializer::class) val data: ByteArray? = null,
    val logs: List<TransactionLog>? = null,
) {

    val isSuccess = code == null || code == 0
}
