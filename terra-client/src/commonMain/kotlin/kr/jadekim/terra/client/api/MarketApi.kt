package kr.jadekim.terra.client.api

import kotlinx.coroutines.Deferred
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.type.Uint128

interface MarketApi {

    fun estimateSwapResult(
        quantity: Uint128,
        offerDenomination: String,
        askDenomination: String,
    ): Deferred<Result<Coin>>
}