package kr.jadekim.terra.bip

expect object Ripemd160 {

    fun hash(data: ByteArray): ByteArray
}