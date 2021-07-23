package money.terra.client.model

import kotlinx.serialization.Serializable
import money.terra.type.ULongAsStringSerializer

@Serializable
data class Result<T>(
    @Serializable(ULongAsStringSerializer::class) val height: ULong,
    val result: T,
)
