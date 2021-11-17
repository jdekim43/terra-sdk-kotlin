package kr.jadekim.terra.bip

expect object Mnemonic {

    fun generate(): String

    fun seedFrom(mnemonic: String, passphrase: String? = null): ByteArray
}