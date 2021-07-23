package money.terra.client.rest.lcd.api

import kotlinx.coroutines.Deferred
import money.terra.client.api.AccountInfo
import money.terra.client.api.AuthApi
import money.terra.client.model.Result
import money.terra.client.rest.HttpClient
import money.terra.model.TypeWrapper

class AuthLcdApi(
    private val client: HttpClient,
) : AuthApi {

    override fun getAccountInfo(address: String): Deferred<Result<TypeWrapper<AccountInfo>>> {
        return client.get("/auth/accounts/$address")
    }
}