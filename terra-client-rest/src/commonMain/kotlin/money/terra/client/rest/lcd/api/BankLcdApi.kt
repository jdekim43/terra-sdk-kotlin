package money.terra.client.rest.lcd.api

import kotlinx.coroutines.Deferred
import money.terra.client.api.BankApi
import money.terra.client.model.Result
import money.terra.client.rest.HttpClient
import money.terra.model.Coin

class BankLcdApi(
    private val client: HttpClient,
) : BankApi {

    override fun getAccountBalances(address: String): Deferred<Result<List<Coin>>> {
        return client.get("/bank/balances/$address")
    }
}