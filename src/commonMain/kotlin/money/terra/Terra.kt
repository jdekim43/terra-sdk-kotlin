package money.terra

import kotlinx.coroutines.Dispatchers
import money.terra.client.TerraClient
import money.terra.transaction.broadcaster.BroadcastResult
import money.terra.transaction.broadcaster.Broadcaster
import money.terra.wallet.ConnectedOwnTerraWallet
import money.terra.wallet.ConnectedTerraWallet
import money.terra.wallet.OwnTerraWallet
import money.terra.wallet.TerraWallet
import kotlin.coroutines.CoroutineContext

class Terra(
    val chainId: String,
    val client: TerraClient,
    val broadcaster: Broadcaster<out BroadcastResult>,
) {

    companion object Builder : ClientBuilder by ClientBuilderImpl()

    fun connect(wallet: TerraWallet) = ConnectedTerraWallet(this, wallet)

    fun connect(wallet: OwnTerraWallet) = ConnectedOwnTerraWallet(this, wallet)

    fun walletFromAddress(address: String) = ConnectedTerraWallet(this, TerraWallet(address))

    fun walletFromKey(
        privateKey: ByteArray,
        publicKey: ByteArray? = null,
    ) = ConnectedOwnTerraWallet(this, OwnTerraWallet(privateKey, publicKey))

    fun walletFromKey(
        privateKey: String,
        publicKey: String? = null,
    ) = ConnectedOwnTerraWallet(this, OwnTerraWallet(privateKey, publicKey))

    fun walletFromMnemonic(
        mnemonic: String,
        account: Int = 0,
        index: Int = 0,
        coinType: Int = COIN_TYPE,
    ) = ConnectedOwnTerraWallet(this, OwnTerraWallet.from(mnemonic, account, index, coinType))

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