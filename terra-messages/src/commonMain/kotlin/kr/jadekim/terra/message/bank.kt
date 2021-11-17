package kr.jadekim.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.model.Message

val BankSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(SendMessage::class)
        subclass(MultipleSendMessage::class)
    }
}

@Serializable
@SerialName("bank/MsgSend")
data class SendMessage(
    @SerialName("from_address") val fromAddress: String,
    @SerialName("to_address") val toAddress: String,
    val amount: List<Coin>,
) : Message()

@Serializable
@SerialName("bank/MsgMultiSend")
data class MultipleSendMessage(
    val inputs: List<Amount>,
    val outputs: List<Amount>,
) : Message(){

    @Serializable
    data class Amount(
        val address: String,
        val coins: List<Coin>,
    )
}