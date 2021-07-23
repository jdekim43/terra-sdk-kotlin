package money.terra.model

import kotlinx.serialization.Serializable
import money.terra.serializer.PolymorphicObjectSerializer

@Serializable(MessageSerializer::class)
abstract class Message

class MessageSerializer : PolymorphicObjectSerializer<Message>(Message::class)