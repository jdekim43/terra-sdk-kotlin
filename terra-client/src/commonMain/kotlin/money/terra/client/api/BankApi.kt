package money.terra.client.api

import kotlinx.coroutines.Deferred
import money.terra.client.model.Result
import money.terra.model.Coin

interface BankApi {

    fun getAccountBalances(address: String): Deferred<Result<List<Coin>>>
}