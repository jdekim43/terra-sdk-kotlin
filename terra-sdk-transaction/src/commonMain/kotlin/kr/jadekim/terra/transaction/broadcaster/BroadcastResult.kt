package kr.jadekim.terra.transaction.broadcaster

import kr.jadekim.terra.model.TransactionLog

interface BroadcastResult {
    val transactionHash: String
    val codeSpace: String?
    val code: Int?
    val rawLog: String?
    val logs: List<TransactionLog>?
}

val BroadcastResult.isSuccess: Boolean
    get() = code == null || code == 0