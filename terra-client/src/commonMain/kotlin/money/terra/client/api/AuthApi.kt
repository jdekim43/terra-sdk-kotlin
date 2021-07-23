package money.terra.client.api

import kotlinx.coroutines.Deferred
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import money.terra.client.model.Result
import money.terra.model.Coin
import money.terra.model.PublicKey
import money.terra.model.TypeWrapper
import money.terra.type.ULongAsStringSerializer

interface AuthApi {

    fun getAccountInfo(address: String): Deferred<Result<TypeWrapper<AccountInfo>>>
}

@Serializable
data class AccountInfo(
    @SerialName("account_number") @Serializable(ULongAsStringSerializer::class) val accountNumber: ULong,
    val address: String,
    val coins: List<Coin>,
    @SerialName("public_key") val publicKey: PublicKey?,
    @Serializable(ULongAsStringSerializer::class) val sequence: ULong,
)