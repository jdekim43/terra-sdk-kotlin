package kr.jadekim.terra.model

import kotlinx.serialization.Serializable
import kr.jadekim.terra.serializer.PolymorphicKeyValueSerializer
import kr.jadekim.terra.serializer.PolymorphicObjectSerializer

@Serializable(MessageSerializer::class)
abstract class Message

object MessageSerializer : PolymorphicObjectSerializer<Message>(Message::class)

@Serializable(WrappedMessageSerializer::class)
abstract class WrappedMessage

object WrappedMessageSerializer : PolymorphicKeyValueSerializer<WrappedMessage>(WrappedMessage::class)
