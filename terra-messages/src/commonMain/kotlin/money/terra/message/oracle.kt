package money.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import money.terra.model.Message

val OracleSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(ExchangeRatePrevoteMessage::class)
        subclass(ExchangeRateVoteMessage::class)
        subclass(DelegateFeedConsentMessage::class)
        subclass(AggregateExchangeRatePrevoteMessage::class)
        subclass(AggregateExchangeRateVote::class)
    }
}

@Serializable
@SerialName("oracle/MsgExchangeRatePrevote")
data class ExchangeRatePrevoteMessage(
    val hash: String,
    val denom: String,
    val feeder: String,
    val validator: String,
) : Message()

@Serializable
@SerialName("oracle/MsgExchangeRateVote")
data class ExchangeRateVoteMessage(
    @SerialName("exchange_rate") val exchangeRate: String,
    val salt: String,
    val denom: String,
    val feeder: String,
    val validator: String,
) : Message()

@Serializable
@SerialName("oracle/MsgDelegateFeedConsent")
data class DelegateFeedConsentMessage(
    val operator: String,
    val delegate: String,
) : Message()

@Serializable
@SerialName("oracle/MsgAggregateExchangeRatePrevote")
data class AggregateExchangeRatePrevoteMessage(
    val hash: String,
    val feeder: String,
    val validator: String,
) : Message()

@Serializable
@SerialName("oracle/MsgAggregateExchangeRateVote")
data class AggregateExchangeRateVote(
    val salt: String,
    @SerialName("exchange_rates") val exchangeRates: String,
    val feeder: String,
    val validator: String,
) : Message()

