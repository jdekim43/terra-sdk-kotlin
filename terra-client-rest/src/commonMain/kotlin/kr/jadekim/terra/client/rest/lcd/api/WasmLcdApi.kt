package kr.jadekim.terra.client.rest.lcd.api

import kotlinx.coroutines.Deferred
import kotlinx.serialization.json.JsonElement
import kr.jadekim.terra.client.api.WasmApi
import kr.jadekim.terra.client.model.Result
import kr.jadekim.terra.client.rest.HttpClient
import kr.jadekim.terra.client.rest.asUrlEncoded

class WasmLcdApi(
    val client: HttpClient,
) : WasmApi {

    override fun <T : Any> query(address: String, message: String): Deferred<Result<JsonElement>> {
        return client.get("/wasm/contracts/$address/store", mapOf("query_msg" to message.asUrlEncoded()))
    }
}