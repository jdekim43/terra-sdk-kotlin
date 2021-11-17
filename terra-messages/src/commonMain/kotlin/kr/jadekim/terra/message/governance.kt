package kr.jadekim.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.model.Message
import kr.jadekim.terra.serializer.PolymorphicObjectSerializer

val GovernanceSerializerModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(SubmitProposalMessage::class)
        subclass(DepositProposalMessage::class)
        subclass(VoteProposalMessage::class)
    }
}

@Serializable(ProposalSerializer::class)
abstract class Proposal

object ProposalSerializer : PolymorphicObjectSerializer<Proposal>(Proposal::class)

@Serializable
@SerialName("gov/MsgSubmitProposal")
data class SubmitProposalMessage(
    val content: Proposal,
    @SerialName("initial_deposit") val initialDeposit: List<Coin>,
    val proposer: String,
) : Message()

@Serializable
@SerialName("gov/MsgDeposit")
data class DepositProposalMessage(
    @SerialName("proposal_id") val proposalId: String,
    val depositor: String,
    val amount: List<Coin>,
) : Message()

@Serializable
@SerialName("gov/MsgVote")
data class VoteProposalMessage(
    @SerialName("proposal_id") val proposalId: String,
    val voter: String,
    val option: String,
) : Message()

