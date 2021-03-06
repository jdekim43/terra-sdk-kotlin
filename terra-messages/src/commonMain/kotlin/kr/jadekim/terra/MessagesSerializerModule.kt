package kr.jadekim.terra

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kr.jadekim.terra.message.*
import kr.jadekim.terra.proposal.DistributionProposalSerializerModule
import kr.jadekim.terra.proposal.GovernanceProposalSerializerModule
import kr.jadekim.terra.proposal.TextProposalSerializerModule
import kr.jadekim.terra.proposal.TreasuryProposalSerializerModule

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