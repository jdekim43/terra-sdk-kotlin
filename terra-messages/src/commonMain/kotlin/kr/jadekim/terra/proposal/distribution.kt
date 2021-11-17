package kr.jadekim.terra.proposal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kr.jadekim.terra.message.Proposal
import kr.jadekim.terra.model.Coin

val DistributionProposalSerializerModule = SerializersModule {
    polymorphic(Proposal::class) {
        subclass(CommunityPoolSpendProposal::class)
    }
}

@Serializable
@SerialName("distribution/CommunityPoolSpendProposal")
data class CommunityPoolSpendProposal(
    val title: String,
    val description: String,
    val recipient: String,
    val amount: List<Coin>,
) : Proposal()