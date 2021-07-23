package money.terra.client.api

import kotlinx.coroutines.Deferred
import money.terra.client.model.Result
import money.terra.type.Decimal
import money.terra.type.Uint128

interface TreasuryApi {

    fun getTaxRate(): Deferred<Result<Decimal>>

    fun getTaxCapacity(denom: String): Deferred<Result<Uint128>>
}