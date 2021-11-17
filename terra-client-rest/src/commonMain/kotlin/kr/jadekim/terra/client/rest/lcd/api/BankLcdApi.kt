package kr.jadekim.terra.client.rest.lcd.api

import kotlinx.coroutines.Deferred
import kr.jadekim.terra.client.api.BankApi
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.client.rest.HttpClient
import kr.jadekim.terra.model.Coin

class BankLcdApi(
    private val client: HttpClient,
) : BankApi {

    override fun getAccountBalances(address: String): Deferred<Result<List<Coin>>> {
        return client.get("/bank/balances/$address")
    }
}