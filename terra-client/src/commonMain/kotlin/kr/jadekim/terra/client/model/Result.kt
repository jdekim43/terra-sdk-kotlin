package kr.jadekim.terra.client.model

import kotlinx.serialization.Serializable
import kr.jadekim.terra.type.ULongAsStringSerializer

@Serializable
data class Result<T>(
    @Serializable(ULongAsStringSerializer::class) val height: ULong,
    val result: T,
)
