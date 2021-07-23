package money.terra.wallet

import kotlinx.coroutines.Dispatchers
import money.terra.Terra
import money.terra.model.Transaction
import kotlin.coroutines.CoroutineContext

open class ConnectedTerraWallet internal constructor(val terra: Terra, origin: TerraWallet) : TerraWallet by origin {

    fun broadcast(
        transaction: Transaction,
        gasAmount: ULong? = null,
        feeDenomination: String? = null,
        coroutineContext: CoroutineContext = Dispatchers.Default,
    ) = terra.broadcaster.broadcast(this, transaction, gasAmount, feeDenomination, coroutineContext)

    fun broadcast(
        gasAmount: ULong? = null,
        feeDenomination: String? = null,
        coroutineContext: CoroutineContext = Dispatchers.Default,
        transactionBuilder: Transaction.Builder.() -> Unit,
    ) = broadcast(Transaction.builder().apply(transactionBuilder).build(), gasAmount, feeDenomination, coroutineContext)

    fun getAccountInfo() = terra.client.authApi.getAccountInfo(address)
}