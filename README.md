# Terra Kotlin SDK
The Terra Software Development Kit (SDK) in Kotlin is a simple library toolkit for building software that can interact
with the Terra blockchain and provides simple abstractions over core data structures, serialization, and API request generation.

## Features
* Support Multiple Platform
  * Kotlin/JVM
  * Java (Experimental)
* Client for LCD, FCD
* Implement tool for concurrency processing for server
* Support customization at core classes
* Modulize components

## Installation (w/ Gradle)
```
repositories {
    maven("https://jadekim.jfrog.io/artifactory/maven")
}
```
```
dependencies {
    implementation("kr.jadekim:terra-sdk:$terraSdkVersion")
    implementation("kr.jadekim:terra-client-rest:$terraSdkVersion")
    
    // or
    
    implementation("kr.jadekim:terra-wallet:$terraSdkVersion")
    implementation("kr.jadekim:terra-messages:$terraSdkVersion")
    implementation("kr.jadekim:terra-sdk-transaction:$terraSdkVersion")
    implementation("kr.jadekim:terra-client-rest:$terraSdkVersion")
}
```

## Usage
### Getting blockchain data
```
dependencies {
    implementation("kr.jadekim:terra-client-rest:$terraSdkVersion")
}
```
```
val client: TerraClient = TerraLcdClient("tequila-0004", "https://tequila-lcd.terra.dev")

val deferredResult: Deferred<Result<Coin>> = client.marketApi.estimateSwapResult(Uint128("10000"), "uluna", "ukrw")
val result: Coin = deferredResult.await().result

println("uluna can be swapped for $result")
```
### Broadcasting transactions
```
dependencies {
    implementation("kr.jadekim:terra-sdk:$terraSdkVersion")
    implementation("kr.jadekim:terra-client-rest:$terraSdkVersion")
}
```
```
    val mnemonic: String = "..."
    val terra: Terra = Terra.fcd("tequila-0004", "https://tequila-fcd.terra.dev").connect()
    val wallet: ConnectedOwnTerraWallet = terra.walletFromMnemonic(mnemonic)
    val receiveWallet: TerraWallet = TerraWallet("terra1x46rqay4d3cssq8gxxvqz8xt6nwlz4td20k38v")
    
    // If use TerraFcdClient, You can broadcast transaction without fee parameter.
    // It will be estimate fee using fcd.
    val deferredResult: Deferred<Pair<BroadcastResult, Transaction>> = wallet.broadcast {
        SendMessage(wallet.address, receiveWallet, listOf(Coin(Uint128(1000), "uluna)).withThis()
    }
    
    // or
    
    val transaction: Transaction = Transaction.builder()
        .message(SendMessage(wallet.address, receiveWallet, listOf(Coin(Uint128(1000), "uluna)))
        .fee(Fee(200000u, listOf(Coin(Uint128("50"), "uluna"))))
        .build()
    val deferredResult: Deferred<Pair<BroadcastResult, Transaction>> = wallet.broadcast(transaction)
    
    val (result, signedTransaction) = deferredResult.await()
    val isSucceedBroadcast = result.isSuccess
    val transactionHash = result.transactionHash
```
### Signing transactions
```
dependencies {
    implementation("kr.jadekim:terra-wallet:$terraSdkVersion")
}
```
```
val mnemonic = "..."
val wallet = OwnTerraWallet.from(mnemonic)
val signData = TransactionSignData(
    chainId = "tequila-0004",
    accountNumber = 0u,
    sequence = 0u,
    fee = Fee(200000u, listOf(Coin(Uint128("50"), "uluna"))),
    messages = listOf(SendMessage(wallet.address, receiveWallet, listOf(Coin(Uint128(1000), "uluna))),
    memo = "",
)

val signature: Signature = wallet.sign(signData)
val signatureText: String = signature.signature
```
### Use remote signer
```
dependencies {
    implementation("kr.jadekim:terra-sdk-transaction:$terraSdkVersion")
}
```
```
class RemoteTransactionSigner(
    chainId: String,
    accountInfoProvider: AccountInfoProvider,
) : TransactionSigner(chainId, accountInfoProvider) {

    override suspend fun sign(accountInfo: AccountInfo, data: TransactionSignData): Signature {
        // TODO: Implement this
    }
}

val client: TerraClient = TerraFcdClient("tequila-0004", "https://tequila-lcd.terra.dev")
val accountInfoProvider: AccountInfoProvider = AlwaysFetchAccountInfoProvider(client)
val signer = RemoteTransactionSigner("tequila-0004", accountInfoProvider)
val terra = Terra.client("tequila-0004", client).provider().signer(singer).connect()
val wallet: ConnectedTerraWallet = terra.walletFromAddress("terra1x46rqay4d3cssq8gxxvqz8xt6nwlz4td20k38v")
val (result, signedTransaction) = wallet.broadcast(...).await()
```

## License
Copyright 2021 jdekim43

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

See the License for the specific language governing permissions and limitations under the License.