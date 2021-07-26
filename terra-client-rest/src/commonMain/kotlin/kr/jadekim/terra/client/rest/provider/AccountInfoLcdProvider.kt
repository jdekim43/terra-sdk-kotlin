package kr.jadekim.terra.client.rest.provider

import kr.jadekim.terra.client.TerraClient
import kr.jadekim.terra.client.rest.lcd.TerraLcdClient
import kr.jadekim.terra.transaction.provider.AccountInfo
import kr.jadekim.terra.transaction.provider.AccountInfoProvider

class AlwaysFetchAccountInfoProvider(private val client: TerraClient) : AccountInfoProvider {

    override suspend fun get(walletAddress: String): AccountInfo {
        return client.authApi.getAccountInfo(walletAddress).await().result.value?.toModel() ?: AccountInfo(walletAddress)
    }

    override suspend fun increaseSequence(walletAddress: String) {
        //do nothing
    }

    override suspend fun refreshSequence(walletAddress: String) {
        //do nothing
    }
}

abstract class CachedAccountInfoProvider(
    private val client: TerraClient,
) : AccountInfoProvider {

    abstract suspend fun getCached(walletAddress: String): AccountInfo?

    abstract suspend fun setCache(walletAddress: String, accountInfo: AccountInfo)

    override suspend fun get(walletAddress: String): AccountInfo {
        var cached = getCached(walletAddress)

        if (cached == null) {
            cached = client.authApi.getAccountInfo(walletAddress).await().result.value?.toModel() ?: AccountInfo(walletAddress)
            setCache(walletAddress, cached)
        }

        return cached
    }

    override suspend fun increaseSequence(walletAddress: String) {
        getCached(walletAddress)?.let {
            setCache(walletAddress, it.copy(sequence = it.sequence + 1u))
        }
    }
}

class LocalCachedAccountInfoProvider(
    client: TerraClient,
) : CachedAccountInfoProvider(client) {

    private val data = mutableMapOf<String, AccountInfo>()

    override suspend fun getCached(walletAddress: String): AccountInfo? = data[walletAddress]

    override suspend fun setCache(walletAddress: String, accountInfo: AccountInfo) {
        data[walletAddress] = accountInfo
    }

    override suspend fun refreshSequence(walletAddress: String) {
        data.clear()
    }
}

private fun kr.jadekim.terra.client.api.AccountInfo.toModel() = AccountInfo(
    address,
    accountNumber,
    publicKey,
    sequence,
)