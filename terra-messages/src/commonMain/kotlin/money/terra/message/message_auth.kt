package money.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import money.terra.model.Coin
import money.terra.model.Message
import money.terra.serializer.PolymorphicObjectSerializer

val MessageAuthSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(RevokeAuthorizationMessage::class)
        subclass(ExecuteAuthorizedMessage::class)
    }

    polymorphic(Authorization::class) {
        subclass(SendAuthorization::class)
    }
}

@Serializable(AuthorizationSerializer::class)
abstract class Authorization

object AuthorizationSerializer : PolymorphicObjectSerializer<Authorization>(Authorization::class)

@Serializable
@SerialName("msgauth/SendAuthorization")
data class SendAuthorization(
    @SerialName("spend_limit") val spendLimit: List<Coin>,
) : Authorization()

@Serializable
@SerialName("msgauth/MsgGrantAuthorization")
class GrantAuthorizationMessage(
    val granter: String,
    val grantee: String,
    val authorization: Authorization,
    val period: String,
) : Message()

@Serializable
@SerialName("msgauth/MsgRevokeAuthorization")
data class RevokeAuthorizationMessage(
    val granter: String,
    val grantee: String,
    @SerialName("authorization_msg_type") val authorizationType: String,
) : Message()

@Serializable
@SerialName("msgauth/MsgExecAuthorized")
class ExecuteAuthorizedMessage(
    val grantee: String,
    @SerialName("msgs") val messages: List<Message>,
) : Message()