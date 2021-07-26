package money.terra.transaction.tool

import kotlinx.coroutines.*
import money.terra.model.Signature
import money.terra.model.Transaction
import money.terra.wallet.OwnTerraWallet
import money.terra.wallet.TerraWallet
import money.terra.wallet.TransactionSignData


open class TransactionSigner(
    val chainId: String,
) {

    @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
    suspend fun sign(wallet: TerraWallet, data: TransactionSignData): Signature {
        throw IllegalStateException("Can't use remote sign")
    }

    suspend fun sign(wallet: TerraWallet, accountNumber: ULong, sequence: ULong, transaction: Transaction): Signature {
        val data = TransactionSignData(transaction, chainId, accountNumber, sequence)

        return sign(wallet, data)
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun sign(
        wallet: OwnTerraWallet,
        accountNumber: ULong,
        sequence: ULong,
        transaction: Transaction,
    ): Signature {
        val data = TransactionSignData(transaction, chainId, accountNumber, sequence)

        return wallet.sign(data)
    }
}

fun Transaction.sign(
    wallet: TerraWallet,
    accountNumber: ULong,
    sequence: ULong,
    signer: TransactionSigner,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Deferred<Transaction> = CoroutineScope(dispatcher).async {
    val signatures = signatures?.toMutableList() ?: mutableListOf()

    signatures.add(signer.sign(wallet, accountNumber, sequence, this@sign))

    copy(signatures = signatures)
}

fun Transaction.sign(
    wallet: OwnTerraWallet,
    accountNumber: ULong,
    sequence: ULong,
    signer: TransactionSigner,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Deferred<Transaction> = CoroutineScope(dispatcher).async {
    val signatures = signatures?.toMutableList() ?: mutableListOf()

    signatures.add(signer.sign(wallet, accountNumber, sequence, this@sign))

    copy(signatures = signatures)
}