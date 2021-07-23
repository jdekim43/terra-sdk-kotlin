package money.terra.client.api

import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.serializer
import money.terra.client.model.Result

interface WasmApi {

    fun <T : Any> query(address: String, message: String): Deferred<Result<JsonElement>>
}

val wasmApiSerializer = Json.Default

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
inline fun <reified T : Any> WasmApi.query(address: String, message: Any): Deferred<Result<T>> {
    val serialized = wasmApiSerializer.encodeToString(mapOf(T::class.serializer().descriptor.serialName to message))
    val response = query<Deferred<Result<JsonElement>>>(address, serialized)

    return CoroutineScope(Dispatchers.Unconfined).async {
        val result = response.await()
        val data = wasmApiSerializer.decodeFromJsonElement<T>(result.result)

        Result(result.height, data)
    }
}