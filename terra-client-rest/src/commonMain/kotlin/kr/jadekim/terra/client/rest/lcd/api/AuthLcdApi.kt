package kr.jadekim.terra.client.rest.lcd.api

import kotlinx.coroutines.Deferred
import kr.jadekim.terra.client.api.AccountInfo
import kr.jadekim.terra.client.api.AuthApi
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.client.rest.HttpClient
import kr.jadekim.terra.model.TypeWrapper

class AuthLcdApi(
    private val client: HttpClient,
) : AuthApi {

    override fun getAccountInfo(address: String): Deferred<Result<TypeWrapper<AccountInfo>>> {
        return client.get("/auth/accounts/$address")
    }
}