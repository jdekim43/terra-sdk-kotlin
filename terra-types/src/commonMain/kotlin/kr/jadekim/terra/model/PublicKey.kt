package kr.jadekim.terra.model

import kotlinx.serialization.Serializable

@Serializable
data class PublicKey(
    val value: String,
    val type: String = "tendermint/PubKeySecp256k1"
)