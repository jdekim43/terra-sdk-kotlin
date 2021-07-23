package money.terra.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kr.jadekim.common.util.encoder.asBase64String
import kr.jadekim.common.util.encoder.decodedBase64

object Base64Serializer : KSerializer<ByteArray> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Base64ByteArray", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray) {
        encoder.encodeString(value.asBase64String)
    }

    override fun deserialize(decoder: Decoder): ByteArray = decoder.decodeString().decodedBase64
}