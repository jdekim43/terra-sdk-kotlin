package money.terra.model

import kotlinx.serialization.Serializable
import money.terra.serializer.PolymorphicKeyValueSerializer
import money.terra.serializer.PolymorphicObjectSerializer

@Serializable(MessageSerializer::class)
abstract class Message

object MessageSerializer : PolymorphicObjectSerializer<Message>(Message::class)

@Serializable(WrappedMessageSerializer::class)
abstract class WrappedMessage

object WrappedMessageSerializer : PolymorphicKeyValueSerializer<WrappedMessage>(WrappedMessage::class)
