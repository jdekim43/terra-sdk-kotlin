package kr.jadekim.terra.client.rest.lcd.api

import kotlinx.coroutines.Deferred
import kr.jadekim.terra.client.api.TreasuryApi
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.client.rest.HttpClient
import kr.jadekim.terra.type.Decimal
import kr.jadekim.terra.type.Uint128

class TreasuryLcdApi(
    private val client: HttpClient,
) : TreasuryApi {

    override fun getTaxRate(): Deferred<Result<Decimal>> {
        return client.get("/treasury/tax_rate")
    }

    override fun getTaxCapacity(denom: String): Deferred<Result<Uint128>> {
        return client.get("/treasury/tax_cap/$denom")
    }
}