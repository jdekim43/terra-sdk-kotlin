package money.terra.transaction.provider

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import money.terra.model.Coin
import money.terra.model.PublicKey
import money.terra.type.ULongAsStringSerializer

interface AccountInfoProvider {

    suspend fun get(walletAddress: String): AccountInfo

    suspend fun increaseSequence(walletAddress: String)

    suspend fun refreshSequence(walletAddress: String)
}

@Serializable
data class AccountInfo(
    val address: String,
    @SerialName("account_number") @Serializable(ULongAsStringSerializer::class) val accountNumber: ULong = 0u,
    val coins: List<Coin> = emptyList(),
    @SerialName("public_key") val publicKey: PublicKey? = null,
    @Serializable(ULongAsStringSerializer::class) val sequence: ULong = 0u,
)