package money.terra

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import money.terra.message.*
import money.terra.proposal.DistributionProposalSerializerModule
import money.terra.proposal.GovernanceProposalSerializerModule
import money.terra.proposal.TextProposalSerializerModule
import money.terra.proposal.TreasuryProposalSerializerModule

internal val TransactionMessageSerializersModule = listOf(
    BankSerializerModule,
    DistributionSerializerModule,
    GovernanceSerializerModule,
    MarketSerializerModule,
    MessageAuthSerializerModule,
    OracleSerializerModule,
    SlashingSerializerModule,
    StakingSerializerModule,
    WasmSerializerModule,
).aggregate()

internal val ProposalMessageSerializersModule = listOf(
    DistributionProposalSerializerModule,
    GovernanceProposalSerializerModule,
    TextProposalSerializerModule,
    TreasuryProposalSerializerModule,
).aggregate()

val MessagesSerializersModule = TransactionMessageSerializersModule + ProposalMessageSerializersModule

private fun List<SerializersModule>.aggregate(): SerializersModule = reduce { acc, module -> acc + module }