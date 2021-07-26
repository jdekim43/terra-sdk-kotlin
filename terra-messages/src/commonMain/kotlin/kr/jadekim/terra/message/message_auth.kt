package kr.jadekim.terra.message

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.model.Message
import kr.jadekim.terra.serializer.PolymorphicObjectSerializer
import kotlin.jvm.JvmStatic

val MessageAuthSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(GrantAuthorizationMessage::class)
        subclass(ExecuteAuthorizedMessage::class)
        subclass(RevokeAuthorizationMessage::class)
    }

    polymorphic(Authorization::class) {
        subclass(GenericAuthorization::class)
        subclass(SendAuthorization::class)
        subclass(StakeAuthorization::class)
    }
}

@Serializable
@SerialName("msgauth/MsgGrantAuthorization")
class GrantAuthorizationMessage(
    val granter: String,
    val grantee: String,
    val grant: MessageGrant,
) : Message()

@Serializable
@SerialName("msgauth/MsgExecAuthorized")
class ExecuteAuthorizedMessage(
    val grantee: String,
    @SerialName("msgs") val messages: List<Message>,
) : Message()

@Serializable
@SerialName("msgauth/MsgRevokeAuthorization")
data class RevokeAuthorizationMessage(
    val granter: String,
    val grantee: String,
    @SerialName("msg_type_url") val messageType: String,
) : Message()

@Serializable
data class MessageGrant(
    val authorization: Authorization,
    val expiration: LocalDateTime,
)

@Serializable(AuthorizationSerializer::class)
abstract class Authorization

object AuthorizationSerializer : PolymorphicObjectSerializer<Authorization>(Authorization::class)

@Serializable
@SerialName("msgauth/GenericAuthorization")
data class GenericAuthorization(
    @SerialName("msg") val message: String,
) : Authorization()

@Serializable
@SerialName("msgauth/SendAuthorization")
data class SendAuthorization(
    @SerialName("spend_limit") val spendLimit: List<Coin>,
) : Authorization()

@Serializable
@SerialName("msgauth/StakeAuthorization")
data class StakeAuthorization(
    @SerialName("max_tokens") val maxTokens: List<Coin>,
    val validators: Validators,
    @SerialName("authorization_type") val authorizationType: AuthorizationType,
) : Authorization() {

    @Serializable
    data class Validators internal constructor(
        @SerialName("allow_list") val allowList: ListWrapper? = null,
        @SerialName("deny_list") val denyList: ListWrapper? = null,
    ) {

        companion object {

            @JvmStatic
            fun allowListOf(validators: List<String>) = Validators(
                allowList = ListWrapper(validators),
            )

            @JvmStatic
            fun denyListOf(validators: List<String>) = Validators(
                denyList = ListWrapper(validators)
            )
        }

        @Serializable
        data class ListWrapper(
            val address: List<String>,
        )
    }

    @Serializable(AuthorizationType.Serializer::class)
    enum class AuthorizationType {
        UNSPECIFIED,
        DELEGATE,
        UNDELEGATE,
        REDELEGATE;

        object Serializer : KSerializer<AuthorizationType> {

            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AuthorizationType", PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: AuthorizationType) {
                encoder.encodeInt(value.ordinal)
            }

            override fun deserialize(decoder: Decoder): AuthorizationType = values()[decoder.decodeInt()]
        }
    }
}
