package kr.jadekim.terra.client.rest.fcd

import kotlinx.coroutines.Deferred
import kr.jadekim.terra.client.TerraClient
import kr.jadekim.terra.client.rest.DEFAULT_TIMEOUT_MILLIS
import kr.jadekim.terra.client.rest.HttpClient
import kr.jadekim.terra.client.rest.lcd.TerraLcdClient
import kr.jadekim.terra.type.Decimal

class TerraFcdClient(
    val chainId: String,
    val httpClient: HttpClient,
    val lcdClient: TerraLcdClient = TerraLcdClient(chainId, httpClient)
) : TerraClient by lcdClient {

    constructor(
        chainId: String,
        serverUrl: String,
        timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS,
    ) : this(chainId, HttpClient(serverUrl, timeoutMillis))

    constructor(
        chainId: String,
        serverUrl: String,
        timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS,
        lcdClient: TerraLcdClient = TerraLcdClient(chainId, serverUrl, timeoutMillis),
    ) : this(chainId, HttpClient(serverUrl, timeoutMillis), lcdClient)

    fun getGasPrices(): Deferred<Map<String, Decimal>> =
        httpClient.get("/v1/txs/gas_prices")
}