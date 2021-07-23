package money.terra.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import money.terra.model.Coin
import money.terra.model.Message
import money.terra.serializer.PolymorphicObjectSerializer

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

