package money.terra.transaction.broadcaster

import kotlinx.coroutines.*
import money.terra.model.Message
import money.terra.model.Transaction
import money.terra.model.TransactionResult
import money.terra.transaction.provider.SemaphoreProvider
import money.terra.transaction.provider.withPermit
import money.terra.transaction.tool.*
import money.terra.wallet.OwnTerraWallet
import money.terra.wallet.TerraWallet
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.math.min

abstract class Broadcaster<Result : BroadcastResult>(
    var signer: TransactionSigner,
    var feeEstimator: FeeEstimator? = null,
    var semaphore: SemaphoreProvider? = null,
) {

    var expectBlockMillis: Long = 7000
    var waitBlockCount: Int = 3

    abstract suspend fun requestBroadcast(transaction: Transaction): Result

    abstract suspend fun queryTransaction(transactionHash: String): TransactionResult?

    open fun broadcast(
        transaction: Transaction,
        coroutineContext: CoroutineContext = Dispatchers.Default,
    ): Deferred<Result> {
        if (transaction.fee == null) {
            throw BroadcastException.EmptyFee
        }

        if (!transaction.isSigned) {
            throw BroadcastException.NotSigned
        }

        return CoroutineScope(coroutineContext).async {
            requestBroadcast(transaction)
        }
    }

    open fun broadcast(
        senderWallet: TerraWallet,
        message: Message,
        memo: String = "",
        gasAmount: ULong? = null,
        feeDenomination: String? = null,
        coroutineContext: CoroutineContext = Dispatchers.Default,
    ) = broadcast(
        senderWallet,
        Transaction(listOf(message), memo),
        gasAmount,
        feeDenomination,
        coroutineContext,
    )

    open fun broadcast(
        senderWallet: OwnTerraWallet,
        message: Message,
        memo: String = "",
        gasAmount: ULong? = null,
        feeDenomination: String? = null,
        coroutineContext: CoroutineContext = Dispatchers.Default,
    ) = broadcast(
        senderWallet,
        Transaction(listOf(message), memo),
        gasAmount,
        feeDenomination,
        coroutineContext,
    )

    open fun broadcast(
        senderWallet: TerraWallet,
        transaction: Transaction,
        gasAmount: ULong? = null,
        feeDenomination: String? = null,
        coroutineContext: CoroutineContext = Dispatchers.Default,
    ): Deferred<Pair<Result, Transaction>> = CoroutineScope(coroutineContext).async {
        @Suppress("LocalVariableName")
        var _transaction = transaction

        if (_transaction.fee == null) {
            if (feeEstimator == null) {
                throw BroadcastException.EmptyFee
            }

            _transaction = try {
                _transaction.estimateFee(feeEstimator!!, gasAmount, feeDenomination).await()
            } catch (e: EstimateFeeException) {
                throw BroadcastException.FailedEstimateFee(e)
            }
        }

        semaphore.lockWallet<Result>(senderWallet.address) {
            if (!_transaction.isSigned) {
                _transaction = _transaction.sign(senderWallet, signer).await()
            }

            broadcast(_transaction).await()
        } to _transaction
    }

    open fun broadcast(
        senderWallet: OwnTerraWallet,
        transaction: Transaction,
        gasAmount: ULong? = null,
        feeDenomination: String? = null,
        coroutineContext: CoroutineContext = Dispatchers.Default,
    ): Deferred<Pair<Result, Transaction>> = CoroutineScope(coroutineContext).async {
        @Suppress("LocalVariableName")
        var _transaction = transaction

        if (_transaction.fee == null) {
            if (feeEstimator == null) {
                throw BroadcastException.EmptyFee
            }

            _transaction = try {
                _transaction.estimateFee(feeEstimator!!, gasAmount, feeDenomination).await()
            } catch (e: EstimateFeeException) {
                throw BroadcastException.FailedEstimateFee(e)
            }
        }

        semaphore.lockWallet<Result>(senderWallet.address) {
            if (!_transaction.isSigned) {
                _transaction = _transaction.sign(senderWallet, signer).await()
            }

            broadcast(_transaction).await()
        } to _transaction
    }

    fun wait(
        transactionHash: String,
        intervalMillis: Long = 1000,
        initialMillis: Long = 6000,
        maxCheckCount: Int? = null,
        coroutineContext: CoroutineContext = Dispatchers.Default,
    ): Deferred<TransactionResult> = CoroutineScope(coroutineContext).async {
        repeat(maxCheckCount ?: calculateMaxCheckCount(initialMillis, intervalMillis, Int.MAX_VALUE)) {
            val transactionResult = queryTransaction(transactionHash)

            if (transactionResult != null) {
                return@async transactionResult
            }

            delay(if (it == 0) initialMillis else intervalMillis)
        }

        throw IllegalStateException("Reach maximum check count")
    }

    protected suspend fun <T> SemaphoreProvider?.lockWallet(
        address: String,
        block: suspend () -> Result,
    ): Result {
        if (this == null) {
            return block()
        }

        val result = try {
            withPermit(address, coroutineContext = coroutineContext, block = block).await()
        } catch (e: Exception) {
            signer.accountInfoProvider.refreshSequence(address)
            throw e
        }

        when {
            result.isSuccess -> signer.accountInfoProvider.increaseSequence(address)
            result.code == 4 -> signer.accountInfoProvider.refreshSequence(address)
        }

        return result
    }

    private fun calculateMaxCheckCount(initialMillis: Long, intervalMillis: Long, request: Int): Int {
        val maxWaitMillis = expectBlockMillis * waitBlockCount
        val maxCheckCount = ((maxWaitMillis - initialMillis) / intervalMillis).toInt() + 1

        return min(maxCheckCount, request)
    }
}
