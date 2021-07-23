package money.terra.client.api

import kotlinx.coroutines.Deferred
import money.terra.client.model.Result
import money.terra.model.Coin
import money.terra.type.Uint128

interface MarketApi {

    fun estimateSwapResult(
        quantity: Uint128,
        offerDenomination: String,
        askDenomination: String,
    ): Deferred<Result<Coin>>
}