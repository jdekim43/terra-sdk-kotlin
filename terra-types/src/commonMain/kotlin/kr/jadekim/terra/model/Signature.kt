package kr.jadekim.terra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.jadekim.common.util.encoder.encodeBase64
import kr.jadekim.terra.type.ULongAsStringSerializer

@Serializable
data class Signature(
    val signature: String,
    @SerialName("pub_key") val publicKey: PublicKey,
    @SerialName("account_number") @Serializable(ULongAsStringSerializer::class) val accountNumber: ULong? = null,
    @Serializable(ULongAsStringSerializer::class) val sequence: ULong? = null,
) {

    constructor(
        signature: ByteArray,
        publicKey: ByteArray,
        accountNumber: ULong? = null,
        sequence: ULong? = null,
        publicKeyType: String = "tendermint/PubKeySecp256k1",
    ) : this(
        signature.encodeBase64(),
        PublicKey(publicKey.encodeBase64(), publicKeyType),
        accountNumber,
        sequence,
    )
}