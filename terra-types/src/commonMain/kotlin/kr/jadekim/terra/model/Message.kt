package kr.jadekim.terra.model

import kotlinx.serialization.Serializable
import kr.jadekim.terra.serializer.PolymorphicKeyValueSerializer
import kr.jadekim.terra.serializer.PolymorphicObjectSerializer

@Serializable(MessageSerializer::class)
abstract class Message

object MessageSerializer : PolymorphicObjectSerializer<Message>(Message::class)

@Serializable(WrappedMessageSerializer::class)
abstract class EnumMessage

object WrappedMessageSerializer : PolymorphicKeyValueSerializer<EnumMessage>(EnumMessage::class)

//TODO: Map 또는 JsonObject 로도 사용 가능하도록. (커스텀해서 사용할 때 SerializersModule 을 매번 등록하는건 번거롭다.)
