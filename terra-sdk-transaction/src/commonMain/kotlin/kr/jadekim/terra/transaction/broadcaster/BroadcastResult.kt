package kr.jadekim.terra.transaction.broadcaster

interface BroadcastResult {
    val height: ULong
    val transactionHash: String
    val code: Int?
}

val BroadcastResult.isSuccess: Boolean
    get() = code == null || code == 0