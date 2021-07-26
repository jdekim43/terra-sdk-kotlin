package money.terra.message

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import money.terra.model.Coin
import money.terra.model.Message
import money.terra.serializer.PolymorphicObjectSerializer

val FeeGrantSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(GrantAllowanceMessage::class)
        subclass(RevokeAllowanceMessage::class)
    }

    polymorphic(Allowance::class) {
        subclass(BasicAllowance::class)
        subclass(PeriodicAllowance::class)
        subclass(AllowedMsgAllowance::class)
    }
}

@Serializable
@SerialName("feegrant/MsgGrantAllowance")
data class GrantAllowanceMessage(
    val granter: String,
    val grantee: String,
    val allowance: Allowance,
) : Message()

@Serializable
@SerialName("feegrant/MsgRevokeAllowance")
data class RevokeAllowanceMessage(
    val granter: String,
    val grantee: String,
) : Message()

@Serializable(AllowanceSerializer::class)
abstract class Allowance

object AllowanceSerializer : PolymorphicObjectSerializer<Allowance>(Allowance::class)

@Serializable
@SerialName("feegrant/BasicAllowance")
data class BasicAllowance(
    @SerialName("spend_limit") val spendLimit: List<Coin>,
    val expiration: LocalDateTime,
) : Allowance()

@Serializable
@SerialName("feegrant/PeriodicAllowance")
data class PeriodicAllowance(
    val basic: BasicAllowance,
    val period: DateTimePeriod,
    @SerialName("period_spend_limit") val periodSpendLimit: List<Coin>,
    @SerialName("period_can_spend") val periodCanSpend: List<Coin>,
    @SerialName("period_reset") val periodReset: LocalDateTime,
) : Allowance()

@Serializable
@SerialName("feegrant/AllowedMsgAllowance")
data class AllowedMsgAllowance(
    val allowance: Allowance,
    @SerialName("allowed_messages") val allowedMessages: List<String>,
) : Allowance()
