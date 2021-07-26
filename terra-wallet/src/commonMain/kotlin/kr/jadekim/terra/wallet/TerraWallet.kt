package kr.jadekim.terra.wallet

import kr.jadekim.terra.COIN_TYPE
import kr.jadekim.terra.bip.Bech32
import kr.jadekim.terra.bip.Bech32Hrp
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface TerraWallet {
    val address: String

    companion object {

        @JvmStatic
        @JvmOverloads
        fun create(account: Int = 0, index: Int = 0) = OwnTerraWallet.create(account, index)

        @JvmStatic
        @JvmOverloads
        fun from(
            mnemonic: String,
            account: Int = 0,
            index: Int = 0,
            coinType: Int = COIN_TYPE,
        ) = OwnTerraWallet.from(mnemonic, account, index, coinType)

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

open class TerraWalletImpl(override val address: String) : TerraWallet {

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
