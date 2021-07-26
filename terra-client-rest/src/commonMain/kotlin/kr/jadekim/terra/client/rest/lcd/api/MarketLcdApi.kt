package kr.jadekim.terra.client.rest.lcd.api

import kotlinx.coroutines.Deferred
import kr.jadekim.terra.client.api.MarketApi
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.client.rest.HttpClient
import kr.jadekim.terra.model.Coin
import kr.jadekim.terra.type.Uint128

class MarketLcdApi(
    private val client: HttpClient,
) : MarketApi {

    override fun estimateSwapResult(
        quantity: Uint128,
        offerDenomination: String,
        askDenomination: String,
    ): Deferred<Result<Coin>> = client.get(
        "/market/swap",
        mapOf("offer_coin" to quantity.toString() + offerDenomination, "ask_denom" to askDenomination),
    )
}