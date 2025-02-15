package com.yaabelozerov.superfinancer.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object Net {
    private val jsonInstance = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val Client = HttpClient {
        install(ContentNegotiation) {
            json(jsonInstance)
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(jsonInstance)
        }
        expectSuccess = true
    }
}