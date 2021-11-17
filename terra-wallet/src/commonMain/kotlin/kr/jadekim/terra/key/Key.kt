package kr.jadekim.terra.key

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kr.jadekim.common.util.encoder.encodeHex
import kr.jadekim.common.util.ext.utf8
import kr.jadekim.common.util.hash.SHA_256
import kr.jadekim.terra.bip.Bip32

interface Key {

    val publicKey: ByteArray

    fun sign(message: ByteArray): Deferred<ByteArray>

    fun sign(message: String): Deferred<ByteArray> = sign(message.utf8())
}

class RawKey(privateKey: ByteArray, publicKey: ByteArray? = null) : Key {

    val privateKey: ByteArray = privateKey.toFixedSize(33)
    val privateKeyHex: String = this.privateKey.encodeHex()

    override val publicKey: ByteArray = publicKey?.toFixedSize(33) ?: Bip32.publicKeyFor(this.privateKey)
    val publicKeyHex: String = this.publicKey.encodeHex()

    fun signSync(message: ByteArray): ByteArray = Bip32.sign(SHA_256.hash(message), privateKey)

    fun signSync(message: String): ByteArray = signSync(message.utf8())

    override fun sign(message: ByteArray): Deferred<ByteArray> = CompletableDeferred(signSync(message))
}

private fun ByteArray.toFixedSize(length: Int) = ByteArray(length).also { copyInto(it) }
