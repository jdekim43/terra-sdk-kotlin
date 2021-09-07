package kr.jadekim.terra

import kotlinx.coroutines.Dispatchers
import kr.jadekim.terra.client.TerraClient
import kr.jadekim.terra.transaction.broadcaster.BroadcastResult
import kr.jadekim.terra.transaction.broadcaster.Broadcaster
import kr.jadekim.terra.wallet.ConnectedTerraWallet
import kr.jadekim.terra.wallet.TerraWallet
import kotlin.coroutines.CoroutineContext

class Terra(
    val chainId: String,
    val client: TerraClient,
    val broadcaster: Broadcaster<out BroadcastResult>,
) {

    companion object Builder : ClientBuilder by ClientBuilderImpl()

    fun connect(wallet: TerraWallet) = ConnectedTerraWallet(this, wallet)

    fun walletFromAddress(address: String) = ConnectedTerraWallet(this, TerraWallet(address))

    fun walletFromRawKey(
        privateKey: ByteArray,
        publicKey: ByteArray? = null,
    ) = ConnectedTerraWallet(this, TerraWallet.fromRawKey(privateKey, publicKey))

    fun walletFromRawKey(
        privateKey: String,
        publicKey: String? = null,
    ) = ConnectedTerraWallet(this, TerraWallet.fromRawKey(privateKey, publicKey))

    fun walletFromMnemonic(
        mnemonic: String,
        account: Int = 0,
        index: Int = 0,
        coinType: Int = COIN_TYPE,
    ) = ConnectedTerraWallet(this, TerraWallet.fromMnemonic(mnemonic, account, index, coinType))

    fun getTransaction(
        transactionHash: String,
    ) = client.transactionApi.getByHash(transactionHash)

    fun waitTransaction(
        transactionHash: String,
        intervalMillis: Long = 1000,
        initialMillis: Long = 6000,
        maxCheckCount: Int? = null,
        coroutineContext: CoroutineContext = Dispatchers.Default,
    ) = broadcaster.wait(
        transactionHash,
        intervalMillis,
        initialMillis,
        maxCheckCount,
        coroutineContext,
    )
}