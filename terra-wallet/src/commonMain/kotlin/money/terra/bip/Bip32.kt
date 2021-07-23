package money.terra.bip

expect object Bip32 {

    fun keyPair(seed: ByteArray, hdPath: IntArray): Bip32KeyPair

    fun publicKeyFor(privateKey: ByteArray): ByteArray

    fun sign(messageHash: ByteArray, privateKey: ByteArray): ByteArray
}

data class Bip32KeyPair(
    val publicKey: ByteArray,
    val privateKey: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Bip32KeyPair

        if (!publicKey.contentEquals(other.publicKey)) return false
        if (!privateKey.contentEquals(other.privateKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = publicKey.contentHashCode()
        result = 31 * result + privateKey.contentHashCode()
        return result
    }
}