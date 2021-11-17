package kr.jadekim.terra.client.rest.lcd

import kr.jadekim.terra.client.TerraClient
import kr.jadekim.terra.client.rest.DEFAULT_TIMEOUT_MILLIS
import kr.jadekim.terra.client.rest.HttpClient
import kr.jadekim.terra.client.rest.lcd.api.*

@Suppress("CanBeParameter")
open class TerraLcdClient(
    val chainId: String,
    val httpClient: HttpClient,
) : TerraClient {

    constructor(
        chainId: String,
        serverUrl: String,
        timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS,
    ) : this(chainId, HttpClient(serverUrl, timeoutMillis))

    override val authApi = AuthLcdApi(httpClient)
    override val bankApi = BankLcdApi(httpClient)
    override val marketApi = MarketLcdApi(httpClient)
    override val transactionApi = TransactionLcdApi(httpClient, chainId)
    override val treasuryApi = TreasuryLcdApi(httpClient)
    override val wasmApi = WasmLcdApi(httpClient)
}