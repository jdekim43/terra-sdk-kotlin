package kr.jadekim.terra.transaction.broadcaster

import kotlinx.coroutines.*
import kr.jadekim.terra.model.Message
import kr.jadekim.terra.model.Transaction
import kr.jadekim.terra.model.TransactionResult
import kr.jadekim.terra.transaction.provider.AccountInfo
import kr.jadekim.terra.transaction.provider.AccountInfoProvider
import kr.jadekim.terra.transaction.provider.SemaphoreProvider
import kr.jadekim.terra.transaction.provider.withPermit
import kr.jadekim.terra.transaction.tool.EstimateFeeException
import kr.jadekim.terra.transaction.tool.FeeEstimator
import kr.jadekim.terra.transaction.tool.TransactionSigner
import kr.jadekim.terra.transaction.tool.sign
import kr.jadekim.terra.wallet.OwnTerraWallet
import kr.jadekim.terra.wallet.TerraWallet
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.math.min

abstract class Broadcaster<Result : BroadcastResult>(
    var accountInfoProvider: AccountInfoProvider,
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
        var accountInfo: AccountInfo? = null

        if (_transaction.fee == null) {
            if (feeEstimator == null) {
                throw BroadcastException.EmptyFee
            }

            accountInfo = accountInfoProvider.get(senderWallet.address)

            _transaction = try {
                if (gasAmount == null) {
                    _transaction.estimateFee(accountInfo, feeDenomination).await()
                } else {
                    _transaction.estimateFee(gasAmount, feeDenomination).await()
                }
            } catch (e: EstimateFeeException) {
                throw BroadcastException.FailedEstimateFee(e)
            }
        }

        semaphore.lockWallet<Result>(senderWallet.address) {
            if (!_transaction.isSigned) {
                if (accountInfo == null) {
                    accountInfo = accountInfoProvider.get(senderWallet.address)
                }

                _transaction = _transaction.sign(
                    senderWallet,
                    accountInfo!!.accountNumber,
                    accountInfo!!.sequence,
                    signer,
                ).await()
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
        var accountInfo: AccountInfo? = null

        if (_transaction.fee == null) {
            if (feeEstimator == null) {
                throw BroadcastException.EmptyFee
            }

            accountInfo = accountInfoProvider.get(senderWallet.address)

            _transaction = try {
                if (gasAmount == null) {
                    _transaction.estimateFee(accountInfo, feeDenomination).await()
                } else {
                    _transaction.estimateFee(gasAmount, feeDenomination).await()
                }
            } catch (e: EstimateFeeException) {
                throw BroadcastException.FailedEstimateFee(e)
            }
        }

        semaphore.lockWallet<Result>(senderWallet.address) {
            if (!_transaction.isSigned) {
                if (accountInfo == null) {
                    accountInfo = accountInfoProvider.get(senderWallet.address)
                }

                _transaction = _transaction.sign(
                    senderWallet,
                    accountInfo!!.accountNumber,
                    accountInfo!!.sequence,
                    signer,
                ).await()
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
            accountInfoProvider.refreshSequence(address)
            throw e
        }

        when {
            result.isSuccess -> accountInfoProvider.increaseSequence(address)
            result.code == 4 -> accountInfoProvider.refreshSequence(address)
        }

        return result
    }

    private fun calculateMaxCheckCount(initialMillis: Long, intervalMillis: Long, request: Int): Int {
        val maxWaitMillis = expectBlockMillis * waitBlockCount
        val maxCheckCount = ((maxWaitMillis - initialMillis) / intervalMillis).toInt() + 1

        return min(maxCheckCount, request)
    }

    @Throws(EstimateFeeException::class)
    fun Transaction.estimateFee(
        senderInfo: AccountInfo,
        feeDenomination: String? = null,
        gasAdjustment: Float? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ): Deferred<Transaction> = CoroutineScope(dispatcher).async {
        val fee = feeEstimator!!.estimate(
            messages,
            senderInfo.address,
            senderInfo.accountNumber,
            senderInfo.sequence,
            feeDenomination ?: feeEstimator!!.defaultFeeDenomination,
            gasAdjustment ?: feeEstimator!!.defaultGasAdjustment,
        )

        this@estimateFee.copy(fee = fee, signatures = null)
    }

    @Throws(EstimateFeeException::class)
    fun Transaction.estimateFee(
        gasAmount: ULong,
        feeDenomination: String? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ): Deferred<Transaction> = CoroutineScope(dispatcher).async {
        val fee = feeEstimator!!.estimate(gasAmount, feeDenomination ?: feeEstimator!!.defaultFeeDenomination)

        this@estimateFee.copy(fee = fee, signatures = null)
    }
}
