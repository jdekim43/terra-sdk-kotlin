package kr.jadekim.terra.model

import kotlinx.serialization.Serializable
import kr.jadekim.terra.model.Attribute

@Serializable
data class TransactionEvent(
    val type: String,
    val attributes: List<Attribute>,
)
