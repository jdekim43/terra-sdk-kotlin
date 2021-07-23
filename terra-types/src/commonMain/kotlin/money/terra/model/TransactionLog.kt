package money.terra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionLog(
    @SerialName("msg_index") val index: UInt,
    val log: String,
    val events: List<TransactionEvent>,
)
