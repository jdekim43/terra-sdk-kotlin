package kr.jadekim.terra.transaction.provider

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.model.PublicKey
import kr.jadekim.terra.type.ULongAsStringSerializer

interface AccountInfoProvider {

    suspend fun get(walletAddress: String): AccountInfo

    suspend fun increaseSequence(walletAddress: String)

    suspend fun refreshSequence(walletAddress: String)
}

@Serializable
data class AccountInfo(
    val address: String,
    @SerialName("account_number") @Serializable(ULongAsStringSerializer::class) val accountNumber: ULong = 0u,
    @SerialName("public_key") val publicKey: PublicKey? = null,
    @Serializable(ULongAsStringSerializer::class) val sequence: ULong = 0u,
)