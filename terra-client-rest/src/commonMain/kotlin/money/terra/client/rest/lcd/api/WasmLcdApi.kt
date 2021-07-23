package money.terra.client.rest.lcd.api

import kotlinx.coroutines.Deferred
import kotlinx.serialization.json.JsonElement
import money.terra.client.api.WasmApi
import money.terra.client.model.Result
import money.terra.client.rest.HttpClient

class WasmLcdApi(
    val client: HttpClient,
) : WasmApi {

    override fun <T : Any> query(address: String, message: String): Deferred<Result<JsonElement>> {
        return client.get("/wasm/contracts/$address/store", mapOf("query_msg" to message))
    }
}