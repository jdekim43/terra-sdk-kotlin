package kr.jadekim.terra.client.broadcaster

import kr.jadekim.terra.client.api.BroadcastSyncResult
import kr.jadekim.terra.client.api.TransactionApi
import kr.jadekim.terra.model.TransactionResult
import kr.jadekim.terra.model.Transaction
import kr.jadekim.terra.transaction.broadcaster.Broadcaster
import kr.jadekim.terra.transaction.provider.AccountInfoProvider
import kr.jadekim.terra.transaction.tool.FeeEstimator
import kr.jadekim.terra.transaction.tool.TransactionSigner
import kr.jadekim.terra.transaction.provider.SemaphoreProvider

class SyncBroadcaster(
    private val transactionApi: TransactionApi,
    accountInfoProvider: AccountInfoProvider,
    signer: TransactionSigner,
    feeEstimator: FeeEstimator? = null,
    semaphore: SemaphoreProvider? = null,
) : Broadcaster<BroadcastSyncResult>(accountInfoProvider, signer, feeEstimator, semaphore) {

    override suspend fun requestBroadcast(transaction: Transaction): BroadcastSyncResult {
        return transactionApi.broadcastSync(transaction).await()
    }

    override suspend fun queryTransaction(transactionHash: String): TransactionResult? {
        return transactionApi.getByHash(transactionHash).await()
    }
}