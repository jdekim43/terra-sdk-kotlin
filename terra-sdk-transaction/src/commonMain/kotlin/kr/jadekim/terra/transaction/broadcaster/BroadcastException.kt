package kr.jadekim.terra.transaction.broadcaster

import kr.jadekim.terra.transaction.tool.EstimateFeeException

sealed class BroadcastException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    object NotSigned : BroadcastException("Not signed transaction")
    object EmptyFee : BroadcastException("Not set fee")
    class FailedEstimateFee(exception: EstimateFeeException) : BroadcastException(exception.reason, exception)
}