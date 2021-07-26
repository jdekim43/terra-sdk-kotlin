package money.terra.test

import io.ktor.client.features.logging.*
import money.terra.Terra
import money.terra.client.rest.HttpClient
import money.terra.message.SendMessage
import money.terra.model.Coin
import money.terra.model.Fee
import money.terra.model.Transaction
import money.terra.transaction.broadcaster.BroadcastException
import money.terra.transaction.broadcaster.isSuccess
import money.terra.type.Uint128
import util.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TerraTest {

    private val client = HttpClient("https://bombay-fcd.terra.dev", logConfig = {
        level = LogLevel.ALL
        logger = Logger.SIMPLE
    })
    private val terraBuilder = Terra.fcd("bombay-0008", client).localCachedAccountInfo().transactionTool()

    @Test
    fun broadcastAsync() = runBlockingTest {
        val terra = terraBuilder.async().connect()
        val wallet = terra.walletFromKey("008FA566B8829B68B29CC35C1EFDED3E163448722117F7D35D32A336612FE166A6")

        val (broadcastResult, transaction) = wallet.broadcast {
            SendMessage(wallet.address, wallet.address, listOf(Coin("uluna", Uint128("1")))).withThis()
        }.await()
        println(broadcastResult)
        println(transaction)
        assertTrue(broadcastResult.isSuccess)

        val transactionResult = terra.waitTransaction(broadcastResult.transactionHash, 200, 4000, null).await()
        println(transactionResult)
        assertTrue(transactionResult.isSuccess)
    }

    @Test
    fun broadcastSync() = runBlockingTest {
        val terra = terraBuilder.sync().connect()
        val wallet = terra.walletFromKey("008FA566B8829B68B29CC35C1EFDED3E163448722117F7D35D32A336612FE166A6")

        val (broadcastResult, transaction) = wallet.broadcast {
            SendMessage(wallet.address, wallet.address, listOf(Coin("uluna", Uint128("1")))).withThis()
        }.await()
        println(broadcastResult)
        println(transaction)
        assertTrue(broadcastResult.isSuccess)

        val transactionResult = terra.waitTransaction(broadcastResult.transactionHash, 200, 4000, null).await()
        println(transactionResult)
        assertTrue(transactionResult.isSuccess)
    }

    @Test
    fun broadcastBlock() = runBlockingTest {
        val terra = terraBuilder.block().connect()
        val wallet = terra.walletFromKey("008FA566B8829B68B29CC35C1EFDED3E163448722117F7D35D32A336612FE166A6")

        val (broadcastResult, transaction) = wallet.broadcast {
            SendMessage(wallet.address, wallet.address, listOf(Coin("uluna", Uint128("1")))).withThis()
        }.await()
        println(broadcastResult)
        println(transaction)
        assertTrue(broadcastResult.isSuccess)

        val transactionResult = terra.waitTransaction(broadcastResult.transactionHash, 200, 4000, null).await()
        println(transactionResult)
        assertTrue(transactionResult.isSuccess)
    }

    @Test
    fun failedTransaction() = runBlockingTest {
        val terra = terraBuilder.async().connect()
        val wallet = terra.walletFromKey("008FA566B8829B68B29CC35C1EFDED3E163448722117F7D35D32A336612FE166A6")

        assertFailsWith(BroadcastException.FailedEstimateFee::class) {
            wallet.broadcast {
                SendMessage(wallet.address, wallet.address, listOf(Coin("ulu", Uint128("1")))).withThis()
            }.await()
        }

        val (broadcastResult, transaction) = wallet.broadcast(gasAmount = 100000u) {
            SendMessage(wallet.address, wallet.address, listOf(Coin("ulu", Uint128("1")))).withThis()
        }.await()
        println(broadcastResult)
        println(transaction)
        assertTrue(broadcastResult.isSuccess)

        val transactionResult = terra.waitTransaction(broadcastResult.transactionHash, 200, 4000, null).await()
        println(transactionResult)
        assertTrue(!transactionResult.isSuccess)
    }
}