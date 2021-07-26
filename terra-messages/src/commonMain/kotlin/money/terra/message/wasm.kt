package money.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kr.jadekim.common.util.encoder.asBase64String
import money.terra.model.Coin
import money.terra.model.Message
import money.terra.model.WrappedMessage
import money.terra.type.ULongAsStringSerializer

val WasmSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(StoreCodeMessage::class)
        subclass(MigrateCodeMessage::class)
        subclass(InstantiateContractMessage::class)
        subclass(ExecuteContractMessage::class)
        subclass(MigrateContractMessage::class)
        subclass(UpdateContractAdminMessage::class)
        subclass(ClearContractAdminMessage::class)
    }
}

@Serializable
@SerialName("wasm/MsgStoreCode")
data class StoreCodeMessage(
    val sender: String,
    @SerialName("wasm_byte_code") val wasmByteCode: String,
) : Message() {

    constructor(sender: String, wasmByteCode: ByteArray) : this(sender, wasmByteCode.asBase64String)
}

@Serializable
@SerialName("wasm/MsgMigrateCode")
data class MigrateCodeMessage(
    @SerialName("code_id") @Serializable(ULongAsStringSerializer::class) val codeId: ULong,
    val sender: String,
    @SerialName("wasm_byte_code") val wasmByteCode: String,
) : Message() {

    constructor(
        codeId: ULong,
        sender: String,
        wasmByteCode: ByteArray,
    ) : this(codeId, sender, wasmByteCode.asBase64String)
}

@Serializable
@SerialName("wasm/MsgInstantiateContract")
data class InstantiateContractMessage(
    val sender: String,
    @SerialName("code_id") @Serializable(ULongAsStringSerializer::class) val codeId: ULong,
    @SerialName("init_msg") val message: JsonObject,
    @SerialName("init_coins") val funds: List<Coin> = emptyList(),
    val admin: String? = null,
) : Message()

@Serializable
@SerialName("wasm/MsgExecuteContract")
data class ExecuteContractMessage(
    val sender: String,
    val contract: String,
    @SerialName("execute_msg") val message: WrappedMessage,
    @SerialName("coins") val funds: List<Coin> = emptyList(),
) : Message()

@Serializable
@SerialName("wasm/MsgMigrateContract")
data class MigrateContractMessage(
    val admin: String,
    val contract: String,
    @SerialName("new_code_id") val newCodeId: String,
    @SerialName("migrate_msg") val message: JsonObject,
) : Message()

@Serializable
@SerialName("wasm/MsgUpdateContractAdmin")
data class UpdateContractAdminMessage(
    val admin: String,
    val newAdmin: String,
    val contract: String,
) : Message()

@Serializable
@SerialName("wasm/MsgClearContractAdmin")
data class ClearContractAdminMessage(
    val admin: String,
    val contract: String,
) : Message()
