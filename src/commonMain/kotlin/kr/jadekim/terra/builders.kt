package kr.jadekim.terra

import kr.jadekim.terra.client.TerraClient
import kr.jadekim.terra.client.broadcaster.AsyncBroadcaster
import kr.jadekim.terra.client.broadcaster.BlockBroadcaster
import kr.jadekim.terra.client.broadcaster.SyncBroadcaster
import kr.jadekim.terra.client.rest.DEFAULT_TIMEOUT_MILLIS
import kr.jadekim.terra.client.rest.HttpClient
import kr.jadekim.terra.client.rest.fcd.TerraFcdClient
import kr.jadekim.terra.client.rest.lcd.TerraLcdClient
import kr.jadekim.terra.client.rest.provider.AlwaysFetchAccountInfoProvider
import kr.jadekim.terra.client.rest.provider.GasPricesFcdProvider
import kr.jadekim.terra.client.rest.provider.LocalCachedAccountInfoProvider
import kr.jadekim.terra.client.transaction.NodeFeeEstimator
import kr.jadekim.terra.transaction.broadcaster.BroadcastResult
import kr.jadekim.terra.transaction.broadcaster.Broadcaster
import kr.jadekim.terra.transaction.provider.*
import kr.jadekim.terra.transaction.tool.FeeEstimator
import kr.jadekim.terra.transaction.tool.TransactionSigner
import kr.jadekim.terra.type.Decimal

interface TerraBuilder {

    fun connect(): Terra
}

interface ClientBuilder : TerraBuilder {

    fun client(chainId: String, client: TerraClient): ClientAndProviderBuilder

    fun lcd(chainId: String, serverUrl: String, timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS): ClientAndProviderBuilder

    fun lcd(chainId: String, httpClient: HttpClient): ClientAndProviderBuilder

    fun fcd(chainId: String, serverUrl: String, timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS): ClientAndProviderBuilder

    fun fcd(chainId: String, httpClient: HttpClient): ClientAndProviderBuilder
}

interface ClientAndProviderBuilder : ClientBuilder, ProviderBuilder {

    fun provider(): ProviderAndTransactionToolBuilder
}

interface ProviderBuilder : TerraBuilder {

    fun accountInfo(accountInfoProvider: AccountInfoProvider): ProviderAndTransactionToolBuilder

    fun alwaysFetchAccountInfo(): ProviderAndTransactionToolBuilder

    fun localCachedAccountInfo(): ProviderAndTransactionToolBuilder

    fun gasPrices(gasPricesProvider: GasPricesProvider): ProviderAndTransactionToolBuilder

    fun staticGasPrices(gasPrices: Map<String, Decimal>): ProviderAndTransactionToolBuilder

    fun fcdGasPrices(fcdClient: TerraFcdClient? = null): ProviderAndTransactionToolBuilder

    fun semaphore(semaphoreProvider: SemaphoreProvider?): ProviderAndTransactionToolBuilder

    fun noSemaphore(): ProviderAndTransactionToolBuilder

    fun localSemaphore(): ProviderAndTransactionToolBuilder
}

interface ProviderAndTransactionToolBuilder : ProviderBuilder, TransactionToolBuilder {

    fun transactionTool(): TransactionToolAndBroadcasterBuilder
}

interface TransactionToolBuilder : TerraBuilder {

    fun feeEstimator(feeEstimator: FeeEstimator): TransactionToolAndBroadcasterBuilder

    fun nodeFeeEstimator(client: TerraClient? = null): TransactionToolAndBroadcasterBuilder

    fun signer(signer: TransactionSigner): TransactionToolAndBroadcasterBuilder

    fun defaultSigner(): TransactionToolAndBroadcasterBuilder
}

interface TransactionToolAndBroadcasterBuilder : TransactionToolBuilder, BroadcasterBuilder {

    fun broadcaster(): BroadcasterBuilder
}

interface BroadcasterBuilder : TerraBuilder {

    fun broadcaster(broadcaster: Broadcaster<out BroadcastResult>): BroadcasterBuilder

    fun async(): BroadcasterBuilder

    fun sync(): BroadcasterBuilder

    fun block(): BroadcasterBuilder
}

class ClientBuilderImpl : ClientAndProviderBuilder {

    internal var chainId: String? = null
    internal var client: TerraClient? = null

    override fun client(chainId: String, client: TerraClient): ClientAndProviderBuilder {
        this.chainId = chainId
        this.client = client

        return this
    }

    override fun lcd(chainId: String, serverUrl: String, timeoutMillis: Long): ClientAndProviderBuilder {
        this.chainId = chainId
        this.client = TerraLcdClient(chainId, serverUrl, timeoutMillis)

        return this
    }

    override fun lcd(chainId: String, httpClient: HttpClient): ClientAndProviderBuilder {
        this.chainId = chainId
        this.client = TerraLcdClient(chainId, httpClient)

        return this
    }

    override fun fcd(chainId: String, serverUrl: String, timeoutMillis: Long): ClientAndProviderBuilder {
        this.chainId = chainId
        this.client = TerraFcdClient(chainId, serverUrl, timeoutMillis)

        return this
    }

    override fun fcd(chainId: String, httpClient: HttpClient): ClientAndProviderBuilder {
        this.chainId = chainId
        this.client = TerraFcdClient(chainId, httpClient)

        return this
    }

    override fun provider(): ProviderAndTransactionToolBuilder {
        requireNotNull(chainId)
        requireNotNull(client)

        return ProviderBuilderImpl(this)
    }

    override fun accountInfo(accountInfoProvider: AccountInfoProvider) = provider().accountInfo(accountInfoProvider)

    override fun alwaysFetchAccountInfo() = provider().alwaysFetchAccountInfo()

    override fun localCachedAccountInfo() = provider().localCachedAccountInfo()

    override fun gasPrices(gasPricesProvider: GasPricesProvider) = provider().gasPrices(gasPricesProvider)

    override fun staticGasPrices(gasPrices: Map<String, Decimal>) = provider().staticGasPrices(gasPrices)

    override fun fcdGasPrices(fcdClient: TerraFcdClient?) = provider().fcdGasPrices(fcdClient)

    override fun semaphore(semaphoreProvider: SemaphoreProvider?) = provider().semaphore(semaphoreProvider)

    override fun noSemaphore() = provider().noSemaphore()

    override fun localSemaphore() = provider().localSemaphore()

    override fun connect() = provider().connect()
}

class ProviderBuilderImpl(
    internal val clientBuilder: ClientBuilderImpl,
) : ProviderAndTransactionToolBuilder {

    private val client: TerraClient = clientBuilder.client!!

    internal var accountInfoProvider: AccountInfoProvider = AlwaysFetchAccountInfoProvider(client)
    internal var gasPricesProvider: GasPricesProvider? =
        if (client is TerraFcdClient) GasPricesFcdProvider(client) else null
    internal var semaphoreProvider: SemaphoreProvider? = null

    override fun accountInfo(accountInfoProvider: AccountInfoProvider): ProviderAndTransactionToolBuilder {
        this.accountInfoProvider = accountInfoProvider

        return this
    }

    override fun alwaysFetchAccountInfo(): ProviderAndTransactionToolBuilder {
        this.accountInfoProvider = AlwaysFetchAccountInfoProvider(client)

        return this
    }

    override fun localCachedAccountInfo(): ProviderAndTransactionToolBuilder {
        this.accountInfoProvider = LocalCachedAccountInfoProvider(client)

        return this
    }

    override fun gasPrices(gasPricesProvider: GasPricesProvider): ProviderAndTransactionToolBuilder {
        this.gasPricesProvider = gasPricesProvider

        return this
    }

    override fun staticGasPrices(gasPrices: Map<String, Decimal>): ProviderAndTransactionToolBuilder {
        this.gasPricesProvider = StaticGasPricesProvider(gasPrices)

        return this
    }

    override fun fcdGasPrices(fcdClient: TerraFcdClient?): ProviderAndTransactionToolBuilder {
        val client = fcdClient ?: client as? TerraFcdClient
        ?: throw IllegalArgumentException("TerraClient is not TerraFcdClient")

        this.gasPricesProvider = GasPricesFcdProvider(client)

        return this
    }

    override fun semaphore(semaphoreProvider: SemaphoreProvider?): ProviderAndTransactionToolBuilder {
        this.semaphoreProvider = semaphoreProvider

        return this
    }

    override fun noSemaphore(): ProviderAndTransactionToolBuilder {
        this.semaphoreProvider = null

        return this
    }

    override fun localSemaphore(): ProviderAndTransactionToolBuilder {
        this.semaphoreProvider = LocalSemaphoreProvider

        return this
    }

    override fun transactionTool(): TransactionToolAndBroadcasterBuilder =
        TransactionToolBuilderImpl(clientBuilder, this)

    override fun feeEstimator(feeEstimator: FeeEstimator) = transactionTool().feeEstimator(feeEstimator)

    override fun nodeFeeEstimator(client: TerraClient?) = transactionTool().nodeFeeEstimator(client)

    override fun signer(signer: TransactionSigner) = transactionTool().signer(signer)

    override fun defaultSigner() = transactionTool().defaultSigner()

    override fun connect() = transactionTool().connect()
}

class TransactionToolBuilderImpl(
    internal val clientBuilder: ClientBuilderImpl,
    internal val providerBuilder: ProviderBuilderImpl,
) : TransactionToolAndBroadcasterBuilder {

    private val chainId: String = clientBuilder.chainId!!
    private val client: TerraClient = clientBuilder.client!!
    private val gasPricesProvider: GasPricesProvider? = providerBuilder.gasPricesProvider

    internal var feeEstimator: FeeEstimator? = gasPricesProvider?.let { NodeFeeEstimator(client.transactionApi, it) }
    internal var signer: TransactionSigner = TransactionSigner(chainId)

    override fun feeEstimator(feeEstimator: FeeEstimator): TransactionToolAndBroadcasterBuilder {
        this.feeEstimator = feeEstimator

        return this
    }

    override fun nodeFeeEstimator(client: TerraClient?): TransactionToolAndBroadcasterBuilder {
        this.feeEstimator = gasPricesProvider?.let { NodeFeeEstimator((client ?: this.client).transactionApi, it) }

        return this
    }

    override fun signer(signer: TransactionSigner): TransactionToolAndBroadcasterBuilder {
        this.signer = signer

        return this
    }

    override fun defaultSigner(): TransactionToolAndBroadcasterBuilder {
        this.signer = TransactionSigner(chainId)

        return this
    }

    override fun broadcaster(): BroadcasterBuilder = BroadcasterBuilderImpl(clientBuilder, providerBuilder, this)

    override fun broadcaster(broadcaster: Broadcaster<out BroadcastResult>) = broadcaster().broadcaster(broadcaster)

    override fun async() = broadcaster().async()

    override fun sync() = broadcaster().sync()

    override fun block() = broadcaster().block()

    override fun connect() = broadcaster().connect()
}

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class BroadcasterBuilderImpl(
    internal val clientBuilder: ClientBuilderImpl,
    internal val providerBuilder: ProviderBuilderImpl,
    internal val transactionToolBuilder: TransactionToolBuilderImpl,
) : BroadcasterBuilder {

    private val chainId: String = clientBuilder.chainId!!
    private val client: TerraClient = clientBuilder.client!!
    private val accountInfoProvider: AccountInfoProvider = providerBuilder.accountInfoProvider
    private val semaphoreProvider: SemaphoreProvider? = providerBuilder.semaphoreProvider
    private val feeEstimator: FeeEstimator? = transactionToolBuilder.feeEstimator
    private val signer: TransactionSigner = transactionToolBuilder.signer

    internal var broadcaster: Broadcaster<out BroadcastResult> = SyncBroadcaster(
        client.transactionApi,
        accountInfoProvider,
        signer,
        feeEstimator,
        semaphoreProvider,
    )

    override fun broadcaster(broadcaster: Broadcaster<out BroadcastResult>): BroadcasterBuilder {
        this.broadcaster = broadcaster

        return this
    }

    override fun async(): BroadcasterBuilder {
        broadcaster = AsyncBroadcaster(
            client.transactionApi,
            accountInfoProvider,
            signer,
            feeEstimator,
            semaphoreProvider,
        )

        return this
    }

    override fun sync(): BroadcasterBuilder {
        broadcaster = SyncBroadcaster(
            client.transactionApi,
            accountInfoProvider,
            signer,
            feeEstimator,
            semaphoreProvider,
        )

        return this
    }

    override fun block(): BroadcasterBuilder {
        broadcaster = BlockBroadcaster(
            client.transactionApi,
            accountInfoProvider,
            signer,
            feeEstimator,
            semaphoreProvider,
        )

        return this
    }

    override fun connect(): Terra = Terra(chainId, client, broadcaster)
}
