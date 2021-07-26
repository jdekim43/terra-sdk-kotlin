package kr.jadekim.terra.wallet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.plus
import kr.jadekim.common.util.encoder.HEX
import kr.jadekim.common.util.hash.SHA_256
import kr.jadekim.terra.BECH32_PUBLIC_KEY_DATA_PREFIX
import kr.jadekim.terra.COIN_TYPE
import kr.jadekim.terra.MessagesSerializersModule
import kr.jadekim.terra.bip.*
import kr.jadekim.terra.model.Fee
import kr.jadekim.terra.model.Message
import kr.jadekim.terra.model.Signature
import kr.jadekim.terra.model.Transaction
import kr.jadekim.terra.type.ULongAsStringSerializer
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface OwnTerraWallet : TerraWallet {

    val privateKey: ByteArray
    val privateKeyHex: String

    val publicKey: ByteArray
    val publicKeyHex: String
    val publicKeyBech32: String

    fun sign(message: String, accountNumber: ULong? = null, sequence: ULong? = null): Signature

    fun sign(signData: TransactionSignData): Signature

    fun sign(transaction: Transaction, chainId: String, accountNumber: ULong, sequence: ULong): Signature

    companion object {

        @JvmStatic
        @JvmOverloads
        fun create(account: Int = 0, index: Int = 0): Pair<OwnTerraWallet, String> {
            val mnemonic = Mnemonic.generate()

            return from(mnemonic, account, index) to mnemonic
        }

        @JvmStatic
        @JvmOverloads
        fun from(mnemonic: String, account: Int = 0, index: Int = 0, coinType: Int = COIN_TYPE): OwnTerraWallet {
            val seed = Mnemonic.seedFrom(mnemonic)
            val hdPathLuna = intArrayOf(44.hard, coinType.hard, account.hard, 0, index)
            val keyPair = Bip32.keyPair(seed, hdPathLuna)

            return OwnTerraWalletImpl(keyPair.privateKey, keyPair.publicKey)
        }

        private val Int.hard
            get() = this or -0x80000000
    }
}

fun OwnTerraWallet(
    privateKey: ByteArray,
    publicKey: ByteArray? = null,
): OwnTerraWallet = OwnTerraWalletImpl(privateKey, publicKey)

fun OwnTerraWallet(
    privateKey: String,
    publicKey: String? = null,
): OwnTerraWallet = OwnTerraWallet(HEX.decode(privateKey), publicKey?.let { HEX.decode(it) })

open class OwnTerraWalletImpl(privateKey: ByteArray, publicKey: ByteArray? = null) : OwnTerraWallet {

    final override val privateKey: ByteArray = privateKey.toFixedSize(33)
    final override val privateKeyHex: String by lazy { HEX.encodeToString(this.privateKey) }

    final override val publicKey: ByteArray = publicKey?.toFixedSize(33) ?: Bip32.publicKeyFor(this.privateKey)
    final override val publicKeyHex: String by lazy { HEX.encodeToString(this.publicKey) }
    final override val publicKeyBech32: String by lazy {
        val data = BECH32_PUBLIC_KEY_DATA_PREFIX + this.publicKey

        Bech32.encode(Bech32Hrp.ACCOUNT_PUBLIC_KEY, Bech32.toWords(data))
    }

    final override val address: String by lazy {
        val hashed = Ripemd160.hash(SHA_256.hash(this.publicKey))

        Bech32.encode(Bech32Hrp.ACCOUNT, Bech32.toWords(hashed))
    }

    override fun sign(message: String, accountNumber: ULong?, sequence: ULong?): Signature {
        val hash = SHA_256.hash(message)
        val signature = Bip32.sign(hash, privateKey)

        return Signature(signature, publicKey, accountNumber, sequence)
    }

    override fun sign(
        signData: TransactionSignData,
    ): Signature = sign(signData.serialize(), signData.accountNumber, signData.sequence)

    override fun sign(
        transaction: Transaction,
        chainId: String,
        accountNumber: ULong,
        sequence: ULong,
    ): Signature = sign(TransactionSignData(transaction, chainId, accountNumber, sequence))

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is TerraWallet) {
            return false
        }

        return address == other.address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}

var SignDataSerializer = Json {
    encodeDefaults = true

    serializersModule += MessagesSerializersModule
}

@Serializable
data class TransactionSignData(
    @SerialName("chain_id") val chainId: String,
    @SerialName("account_number") @Serializable(ULongAsStringSerializer::class) val accountNumber: ULong,
    @Serializable(ULongAsStringSerializer::class) val sequence: ULong,
    val fee: Fee,
    @SerialName("msgs") val messages: List<Message>,
    val memo: String,
) {

    constructor(
        transaction: Transaction,
        chainId: String,
        accountNumber: ULong,
        sequence: ULong,
    ) : this(
        chainId,
        accountNumber,
        sequence,
        transaction.fee!!,
        transaction.messages,
        transaction.memo,
    )

    fun serialize(): String = SignDataSerializer.encodeToJsonElement(this).sorted()
        .let { SignDataSerializer.encodeToString(it) }

    private fun JsonElement.sorted(): JsonElement {
        return when (this) {
            is JsonPrimitive -> this
            JsonNull -> this
            is JsonObject -> {
                val sortedMap = LinkedHashMap<String, JsonElement>()

                keys.sorted()
                    .forEach { key -> sortedMap[key] = getValue(key).sorted() }

                JsonObject(sortedMap)
            }
            is JsonArray -> JsonArray(map { it.sorted() })
        }
    }
}

private fun ByteArray.toFixedSize(length: Int) = ByteArray(length).also { copyInto(it) }
