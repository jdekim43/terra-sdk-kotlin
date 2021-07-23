package money.terra.client

import money.terra.client.api.*

interface TerraClient {

    val authApi: AuthApi
    val bankApi: BankApi
    val marketApi: MarketApi
    val transactionApi: TransactionApi
    val treasuryApi: TreasuryApi
    val wasmApi: WasmApi
}