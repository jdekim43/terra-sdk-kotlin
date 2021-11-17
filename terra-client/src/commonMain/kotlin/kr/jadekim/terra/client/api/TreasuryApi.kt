package kr.jadekim.terra.client.api

import kotlinx.coroutines.Deferred
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.type.Decimal
import kr.jadekim.terra.type.Uint128

interface TreasuryApi {

    fun getTaxRate(): Deferred<Result<Decimal>>

    fun getTaxCapacity(denom: String): Deferred<Result<Uint128>>
}