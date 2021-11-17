package kr.jadekim.terra.client.rest.lcd.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.SerializationException
import kr.jadekim.terra.client.api.AccountInfo
import kr.jadekim.terra.client.api.AuthApi
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.client.rest.HttpClient
import kr.jadekim.terra.model.TypeWrapper

class AuthLcdApi(
    private val client: HttpClient,
) : AuthApi {

    override fun getAccountInfo(address: String): Deferred<Result<TypeWrapper<AccountInfo?>>> =
        CoroutineScope(Dispatchers.Unconfined).async {
            try {
                client.get<Result<TypeWrapper<AccountInfo?>>>("/auth/accounts/$address").await()
            } catch (e: SerializationException) {
                if (e.message?.endsWith("missing") == true) {
                    return@async Result(0u, TypeWrapper("core/Account", null))
                }

                throw e
            }
        }
}