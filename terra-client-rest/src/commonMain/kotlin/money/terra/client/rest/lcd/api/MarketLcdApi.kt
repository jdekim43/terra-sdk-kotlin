package money.terra.client.rest.lcd.api

import kotlinx.coroutines.Deferred
import money.terra.client.api.MarketApi
import money.terra.client.model.Result
import money.terra.client.rest.HttpClient
import money.terra.model.Coin
import money.terra.type.Uint128

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