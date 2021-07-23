package money.terra.transaction.tool

import kotlinx.coroutines.*
import money.terra.model.Signature
import money.terra.model.Transaction
import money.terra.transaction.provider.AccountInfo
import money.terra.transaction.provider.AccountInfoProvider
import money.terra.wallet.OwnTerraWallet
import money.terra.wallet.TerraWallet
import money.terra.wallet.TransactionSignData


open class TransactionSigner(
    val chainId: String,
    val accountInfoProvider: AccountInfoProvider,
) {

    suspend fun sign(accountInfo: AccountInfo, data: TransactionSignData): Signature {
        throw IllegalStateException("Can't use remote sign")
    }

    suspend fun sign(wallet: TerraWallet, transaction: Transaction): Signature {
        val accountInfo = accountInfoProvider.get(wallet.address)
        val data = TransactionSignData(transaction, chainId, accountInfo.accountNumber, accountInfo.sequence)

        return sign(accountInfo, data)
    }

    suspend fun sign(wallet: OwnTerraWallet, transaction: Transaction): Signature {
        val accountInfo = accountInfoProvider.get(wallet.address)
        val data = TransactionSignData(transaction, chainId, accountInfo.accountNumber, accountInfo.sequence)

        return wallet.sign(data)
    }
}

fun Transaction.sign(
    wallet: TerraWallet,
    signer: TransactionSigner,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Deferred<Transaction> = CoroutineScope(dispatcher).async {
    val signatures = signatures?.toMutableList() ?: mutableListOf()

    signatures.add(signer.sign(wallet, this@sign))

    copy(signatures = signatures)
}

fun Transaction.sign(
    wallet: OwnTerraWallet,
    signer: TransactionSigner,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Deferred<Transaction> = CoroutineScope(dispatcher).async {
    val signatures = signatures?.toMutableList() ?: mutableListOf()

    signatures.add(signer.sign(wallet, this@sign))

    copy(signatures = signatures)
}