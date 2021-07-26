package money.terra.wallet

import money.terra.Terra
import money.terra.model.Transaction
import kotlin.coroutines.CoroutineContext

class ConnectedOwnTerraWallet internal constructor(
    terra: Terra,
    private val origin: OwnTerraWallet,
) : ConnectedTerraWallet(terra, origin), OwnTerraWallet by origin {

    override val address: String = origin.address

    override fun broadcast(
        transaction: Transaction,
        gasAmount: ULong?,
        feeDenomination: String?,
        coroutineContext: CoroutineContext,
    ) = terra.broadcaster.broadcast(this, transaction, gasAmount, feeDenomination, coroutineContext)

    override fun broadcast(
        gasAmount: ULong?,
        feeDenomination: String?,
        coroutineContext: CoroutineContext,
        transactionBuilder: Transaction.Builder.() -> Unit,
    ) = broadcast(Transaction.builder().apply(transactionBuilder).build(), gasAmount, feeDenomination, coroutineContext)
}