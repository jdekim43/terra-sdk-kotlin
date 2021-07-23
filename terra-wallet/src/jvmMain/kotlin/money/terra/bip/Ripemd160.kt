package money.terra.bip

import io.nayuki.bitcoin.crypto.Ripemd160

actual object Ripemd160 {

    actual fun hash(data: ByteArray): ByteArray = Ripemd160.getHash(data)
}