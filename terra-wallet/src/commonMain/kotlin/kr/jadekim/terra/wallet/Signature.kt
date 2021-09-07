package kr.jadekim.terra.wallet

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.overwriteWith
import kotlinx.serialization.modules.plus
import kr.jadekim.terra.MessagesSerializersModule
import kr.jadekim.terra.key.Key
import kr.jadekim.terra.model.Fee
import kr.jadekim.terra.model.Message
import kr.jadekim.terra.model.Signature
import kr.jadekim.terra.model.Transaction
import kr.jadekim.terra.type.ULongAsStringSerializer


private val SignatureScope = CoroutineScope(Dispatchers.Default)

fun Key.sign(message: String, accountNumber: ULong?, sequence: ULong?): Deferred<Signature> = SignatureScope.async {
    val signature = sign(message).await()

    Signature(signature, publicKey, accountNumber, sequence)
}

fun Key.sign(
    signData: TransactionSignData,
): Deferred<Signature> = sign(signData.serialize(), signData.accountNumber, signData.sequence)

fun Key.sign(
    transaction: Transaction,
    chainId: String,
    accountNumber: ULong,
    sequence: ULong,
): Deferred<Signature> = sign(TransactionSignData(transaction, chainId, accountNumber, sequence))

@Serializable
data class TransactionSignData(
    @SerialName("chain_id") val chainId: String,
    @SerialName("account_number") @Serializable(ULongAsStringSerializer::class) val accountNumber: ULong,
    @Serializable(ULongAsStringSerializer::class) val sequence: ULong,
    val fee: Fee,
    @SerialName("msgs") val messages: List<Message>,
    val memo: String,
) {

    companion object {
        var serializersModule: SerializersModule = MessagesSerializersModule
            set(value) {
                field = value

                serializer = Json(serializer) {
                    serializersModule.overwriteWith(this@Companion.serializersModule)
                }
            }

        var serializer: Json = Json {
            encodeDefaults = true

            serializersModule += this@Companion.serializersModule
        }
    }

    constructor(
        transaction: Transaction,
        chainId: String,
        accountNumber: ULong,
        sequence: ULong,
    ) : this(
        chainId,
        accountNumber,
        sequence,
        transaction.fee!!,
        transaction.messages,
        transaction.memo,
    )

    fun serialize(): String = serializer.encodeToJsonElement(this).sorted()
        .let { serializer.encodeToString(JsonElement.serializer(), it) }

    private fun JsonElement.sorted(): JsonElement {
        return when (this) {
            is JsonPrimitive -> this
            JsonNull -> this
            is JsonObject -> {
                val sortedMap = LinkedHashMap<String, JsonElement>()

                keys.sorted()
                    .forEach { key -> sortedMap[key] = getValue(key).sorted() }

                JsonObject(sortedMap)
            }
            is JsonArray -> JsonArray(map { it.sorted() })
        }
    }
}