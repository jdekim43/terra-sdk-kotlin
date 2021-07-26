package kr.jadekim.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.model.Message

val DistributionSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(SetWithdrawAddressMessage::class)
        subclass(WithdrawDelegatorRewardMessage::class)
        subclass(WithdrawValidatorCommissionMessage::class)
        subclass(FundCommunityPoolMessage::class)
    }
}

@Serializable
@SerialName("distribution/MsgModifyWithdrawAddress")
data class SetWithdrawAddressMessage(
    @SerialName("delegator_address") val delegatorAddress: String,
    @SerialName("withdraw_address") val withdrawAddress: String,
) : Message()

@Serializable
@SerialName("distribution/MsgWithdrawDelegationReward")
data class WithdrawDelegatorRewardMessage(
    @SerialName("delegator_address") val delegatorAddress: String,
    @SerialName("validator_address") val validatorAddress: String,
) : Message()

@Serializable
@SerialName("distribution/MsgWithdrawValidatorCommission")
data class WithdrawValidatorCommissionMessage(
    @SerialName("validator_address") val validatorAddress: String,
) : Message()

@Serializable
@SerialName("distribution/MsgFundCommunityPool")
data class FundCommunityPoolMessage(
    val amount: List<Coin>,
    val depositor: String,
) : Message()