package kr.jadekim.terra.client.api

import kotlinx.coroutines.Deferred
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.model.Coin

interface BankApi {

    fun getAccountBalances(address: String): Deferred<Result<List<Coin>>>
}