package kr.jadekim.terra.wallet

import kr.jadekim.common.util.encoder.decodeHex
import kr.jadekim.common.util.hash.SHA_256
import kr.jadekim.terra.BECH32_PUBLIC_KEY_DATA_PREFIX
import kr.jadekim.terra.COIN_TYPE
import kr.jadekim.terra.bip.*
import kr.jadekim.terra.key.Key
import kr.jadekim.terra.key.RawKey
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface TerraWallet {

    val address: String
    val key: Key?

    companion object {

        @JvmStatic
        @JvmOverloads
        fun create(account: Int = 0, index: Int = 0): Pair<TerraWallet, String> {
            val mnemonic = Mnemonic.generate()

            return fromMnemonic(mnemonic, account, index) to mnemonic
        }

        @JvmStatic
        @JvmOverloads
        fun fromMnemonic(
            mnemonic: String,
            account: Int = 0,
            index: Int = 0,
            coinType: Int = COIN_TYPE,
        ): TerraWallet {
            val seed = Mnemonic.seedFrom(mnemonic)
            val hdPathLuna = intArrayOf(44.hard, coinType.hard, account.hard, 0, index)
            val keyPair = Bip32.keyPair(seed, hdPathLuna)

            return fromRawKey(keyPair.privateKey, keyPair.publicKey)
        }

        @JvmStatic
        @JvmOverloads
        fun fromRawKey(privateKey: ByteArray, publicKey: ByteArray? = null) = TerraWallet(RawKey(privateKey, publicKey))

        @JvmStatic
        @JvmOverloads
        fun fromRawKey(
            privateKey: String,
            publicKey: String? = null,
        ) = fromRawKey(privateKey.decodeHex(), publicKey?.decodeHex())

        @JvmStatic
        fun isValidAddress(address: String): Boolean = try {
            val (hrp, _) = Bech32.decode(address)
            hrp == Bech32Hrp.ACCOUNT
        } catch (e: Exception) {
            false
        }
    }
}

fun TerraWallet(address: String): TerraWallet = TerraWalletImpl(address)

fun TerraWallet(key: Key): TerraWallet = TerraWalletImpl(key)

class TerraWalletImpl private constructor (override val address: String, override val key: Key?) : TerraWallet {

    constructor(address: String) : this(address, null)

    constructor(key: Key) : this(key.accountAddress, key)

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is TerraWallet) {
            return false
        }

        return address == other.address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}

val Key.accountAddress: String
    get() {
        val hashed = Ripemd160.hash(SHA_256.hash(this.publicKey))

        return Bech32.encode(Bech32Hrp.ACCOUNT, Bech32.toWords(hashed))
    }

val Key.accountPublicKey: String
    get() {
        val data = BECH32_PUBLIC_KEY_DATA_PREFIX + this.publicKey

        return Bech32.encode(Bech32Hrp.ACCOUNT_PUBLIC_KEY, Bech32.toWords(data))
    }

private val Int.hard
    get() = this or -0x80000000
