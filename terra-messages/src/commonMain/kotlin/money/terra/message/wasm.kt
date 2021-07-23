package money.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kr.jadekim.common.util.encoder.asBase64String
import money.terra.model.Coin
import money.terra.model.Message

val WasmSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(StoreCodeMessage::class)
        subclass(InstantiateContract::class)
        subclass(ExecuteContract::class)
        subclass(MigrateContract::class)
        subclass(UpdateContractOwner::class)
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
@SerialName("wasm/MsgInstantiateContract")
data class InstantiateContract(
    val owner: String,
    @SerialName("code_id") val codeId: String,
    @SerialName("init_msg") val initMessage: String,
    @SerialName("init_coins") val initCoins: List<Coin>,
    val migratable: Boolean,
) : Message()

@Serializable
@SerialName("wasm/MsgExecuteContract")
data class ExecuteContract(
    val sender: String,
    val contract: String,
    @SerialName("execute_msg") val executeMessage: String,
    val coins: List<Coin>,
) : Message()

@Serializable
@SerialName("wasm/MsgMigrateContract")
data class MigrateContract(
    val owner: String,
    val contract: String,
    @SerialName("new_code_id") val newCodeId: String,
    @SerialName("migrate_msg") val migrateMsg: String,
) : Message()

@Serializable
@SerialName("wasm/MsgUpdateContractOwner")
data class UpdateContractOwner(
    val owner: String,
    @SerialName("new_owner") val newOwner: String,
    val contract: String,
) : Message()