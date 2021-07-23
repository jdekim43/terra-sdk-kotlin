package money.terra.client.broadcaster

import money.terra.client.api.BroadcastBlockResult
import money.terra.client.api.TransactionApi
import money.terra.model.TransactionResult
import money.terra.model.Transaction
import money.terra.transaction.broadcaster.Broadcaster
import money.terra.transaction.tool.FeeEstimator
import money.terra.transaction.tool.TransactionSigner
import money.terra.transaction.provider.SemaphoreProvider

class BlockBroadcaster(
    private val transactionApi: TransactionApi,
    signer: TransactionSigner,
    feeEstimator: FeeEstimator? = null,
    semaphore: SemaphoreProvider? = null,
) : Broadcaster<BroadcastBlockResult>(signer, feeEstimator, semaphore) {

    override suspend fun requestBroadcast(transaction: Transaction): BroadcastBlockResult {
        return transactionApi.broadcastBlock(transaction).await()
    }

    override suspend fun queryTransaction(transactionHash: String): TransactionResult? {
        return transactionApi.getByHash(transactionHash).await()
    }
}