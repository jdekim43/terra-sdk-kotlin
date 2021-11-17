package kr.jadekim.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.model.Message

val MarketSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(SwapMessage::class)
        subclass(SwapSendMessage::class)
    }
}

@Serializable
@SerialName("market/MsgSwap")
data class SwapMessage(
    val trader: String,
    @SerialName("offer_coin") val offerCoin: Coin,
    @SerialName("ask_denom") val askDenomination: String,
) : Message()

@Serializable
@SerialName("market/MsgSwapSend")
data class SwapSendMessage(
    @SerialName("from_address") val fromAddress: String,
    @SerialName("to_address") val toAddress: String,
    @SerialName("offer_coin") val offerCoin: Coin,
    @SerialName("ask_denom") val askDenomination: String,
) : Message()

