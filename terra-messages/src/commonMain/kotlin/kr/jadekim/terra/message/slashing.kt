package kr.jadekim.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kr.jadekim.terra.model.Message

val SlashingSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(UnjailMessage::class)
    }
}

@Serializable
@SerialName("cosmos/MsgUnjail")
data class UnjailMessage(
    val address: String,
) : Message()