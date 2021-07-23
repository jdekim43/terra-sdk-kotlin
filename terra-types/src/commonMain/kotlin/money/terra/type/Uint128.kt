package money.terra.type

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Uint128Serializer::class)
expect class Uint128(value: String) : Number, Comparable<Uint128>

expect operator fun Uint128.times(other: Uint128): Uint128

fun ULong.toUint128(): Uint128 = Uint128(toString())

object Uint128Serializer : KSerializer<Uint128> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Uint128", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Uint128) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uint128 = Uint128(decoder.decodeString())
}
