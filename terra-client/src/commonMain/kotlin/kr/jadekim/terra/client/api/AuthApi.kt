package kr.jadekim.terra.client.api

import kotlinx.coroutines.Deferred
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.model.PublicKey
import kr.jadekim.terra.model.TypeWrapper
import kr.jadekim.terra.type.ULongAsStringSerializer

interface AuthApi {

    fun getAccountInfo(address: String): Deferred<Result<TypeWrapper<AccountInfo?>>>
}

@Serializable
@SerialName("core/Account")
data class AccountInfo(
    val address: String,
    @SerialName("account_number") @Serializable(ULongAsStringSerializer::class) val accountNumber: ULong,
    @SerialName("public_key") val publicKey: PublicKey? = null,
    @Serializable(ULongAsStringSerializer::class) val sequence: ULong = 0u,
)