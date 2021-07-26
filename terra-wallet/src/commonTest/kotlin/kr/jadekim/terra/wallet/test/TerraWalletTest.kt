package kr.jadekim.terra.wallet.test

import kr.jadekim.terra.wallet.TerraWallet
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TerraWalletTest {

    @Test
    fun succeedValidAddress() {
        assertTrue {
            TerraWallet.isValidAddress("terra1ra2flyqkklt75z43j2030n08h2ctgqnfalzngy")
        }
    }

    @Test
    fun failedInvalidAddress() {
        assertFalse { TerraWallet.isValidAddress("terra1ra2flyqkklt75z43j2030n08h2ctgqnfalzng") }
        assertFalse { TerraWallet.isValidAddress("erra1ra2flyqkklt75z43j2030n08h2ctgqnfalzngy") }
        assertFalse { TerraWallet.isValidAddress("terrapub1ra2flyqkklt75z43j2030n08h2ctgqnfalzngy") }
        assertFalse { TerraWallet.isValidAddress("terrara2flyqkklt75z43j2030n08h2ctgqnfalzngy") }
        assertFalse { TerraWallet.isValidAddress("terra1") }
        assertFalse { TerraWallet.isValidAddress("1ra2flyqkklt75z43j2030n08h2ctgqnfalzngy") }
        assertFalse { TerraWallet.isValidAddress("terra1r") }
        assertFalse { TerraWallet.isValidAddress("tea1r") }
        assertFalse { TerraWallet.isValidAddress("tear") }
    }
}