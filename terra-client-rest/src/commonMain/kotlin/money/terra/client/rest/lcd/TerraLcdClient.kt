package money.terra.client.rest.lcd

import money.terra.client.TerraClient
import money.terra.client.rest.DEFAULT_TIMEOUT_MILLIS
import money.terra.client.rest.HttpClient
import money.terra.client.rest.lcd.api.*

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
    override val transactionApi = TransactionLcdApi(httpClient)
    override val treasuryApi = TreasuryLcdApi(httpClient)
    override val wasmApi = WasmLcdApi(httpClient)
}