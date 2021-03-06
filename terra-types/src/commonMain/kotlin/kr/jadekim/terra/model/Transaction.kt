package kr.jadekim.terra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.jadekim.common.util.ext.hasValue
import kr.jadekim.terra.type.ULongAsStringSerializer
import kotlin.jvm.JvmStatic

@Serializable
@SerialName("core/StdTx")
data class Transaction(
    @SerialName("msg") val messages: List<Message>,
    val memo: String = "",
    val fee: Fee? = null,
    val signatures: List<Signature>? = null,
    @SerialName("timeout_height") @Serializable(ULongAsStringSerializer::class) val timeoutHeight: ULong = 0u,
) {
    val isSigned: Boolean
        get() = signatures.hasValue()

    companion object {

        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {

        private var fee: Fee? = null
        private var memo: String = ""
        private var messages: MutableList<Message> = mutableListOf()

        fun fee(fee: Fee): Builder {
            this.fee = fee

            return this
        }

        fun memo(memo: String): Builder {
            this.memo = memo

            return this
        }

        fun message(vararg message: Message): Builder {
            messages.addAll(message)

            return this
        }

        fun message(messages: List<Message>): Builder {
            this.messages.addAll(messages)

            return this
        }

        fun Message.withThis() {
            messages.add(this)
        }

        fun build() = Transaction(
            messages.toList(),
            memo,
            fee,
        )
    }
}
