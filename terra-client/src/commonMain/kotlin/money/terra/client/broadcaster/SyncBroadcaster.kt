package money.terra.client.broadcaster

import money.terra.client.api.BroadcastSyncResult
import money.terra.client.api.TransactionApi
import money.terra.model.TransactionResult
import money.terra.model.Transaction
import money.terra.transaction.broadcaster.Broadcaster
import money.terra.transaction.tool.FeeEstimator
import money.terra.transaction.tool.TransactionSigner
import money.terra.transaction.provider.SemaphoreProvider

class SyncBroadcaster(
    private val transactionApi: TransactionApi,
    signer: TransactionSigner,
    feeEstimator: FeeEstimator? = null,
    semaphore: SemaphoreProvider? = null,
) : Broadcaster<BroadcastSyncResult>(signer, feeEstimator, semaphore) {

    override suspend fun requestBroadcast(transaction: Transaction): BroadcastSyncResult {
        return transactionApi.broadcastSync(transaction).await()
    }

    override suspend fun queryTransaction(transactionHash: String): TransactionResult? {
        return transactionApi.getByHash(transactionHash).await()
    }
}