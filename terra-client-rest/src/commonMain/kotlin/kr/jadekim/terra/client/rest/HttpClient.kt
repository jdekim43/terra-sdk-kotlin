package kr.jadekim.terra.client.rest

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import kr.jadekim.terra.MessagesSerializersModule
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

internal expect val ENGINE_FACTORY: EngineFactory<HttpClientEngineConfig>

internal expect fun String.asUrlEncoded(): String

const val DEFAULT_TIMEOUT_MILLIS: Long = 10000

val HTTP_CLIENT_DEFAULT_JSON_SERIALIZER: Json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    serializersModule += MessagesSerializersModule
}

class EngineFactory<T : HttpClientEngineConfig>(
    val engine: HttpClientEngineFactory<T>,
    val configure: T.() -> Unit = {},
)

class HttpClient(
    val serverUrl: String,
    val timeoutMillis: Long = 10000,
    val json: Json = HTTP_CLIENT_DEFAULT_JSON_SERIALIZER,
    val logConfig: (Logging.Config.() -> Unit)? = null,
    override val coroutineContext: CoroutineContext = Dispatchers.Default,
) : CoroutineScope {

    val baseUrl: String = if (serverUrl.endsWith("/")) serverUrl.dropLast(1) else serverUrl

    val server = HttpClient(ENGINE_FACTORY.engine) {
        engine(ENGINE_FACTORY.configure)

        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
        }

        if (logConfig != null) {
            install(Logging, logConfig)
        }
    }

    inline fun <reified T> get(
        path: String,
        queryParam: Map<String, String?> = emptyMap(),
    ): Deferred<T> {
        val query = if (queryParam.isEmpty()) {
            ""
        } else {
            queryParam.entries.joinToString("&", "?") { "${it.key}=${it.value}" }
        }

        return async { server.get(baseUrl + path + query) }
    }

    inline fun <reified T> post(
        path: String,
        body: Any,
    ): Deferred<T> = async {
        server.post(baseUrl + path) {
            contentType(ContentType.Application.Json)

            this.body = body
        }
    }
}