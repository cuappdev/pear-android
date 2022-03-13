package com.cornellappdev.coffee_chats_android.networking

import com.cornellappdev.coffee_chats_android.BuildConfig
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request
import okhttp3.RequestBody

enum class EndpointMethod {
    GET, POST, DELETE
}

class Endpoint(
    private val path: String,
    private val headers: Map<String, String> = mapOf(),
    private val body: RequestBody? = null,
    private val method: EndpointMethod,
    private val useDefaultHost: Boolean = true
) {
    private val host = "https://${BuildConfig.BACKEND_URI}/api"

    companion object

    fun okHttpRequest(): Request {
        val endpoint = if (useDefaultHost) host + path else "https://$path"
        val headers = headers.toHeaders()

        return when (method) {
            EndpointMethod.GET -> {
                Request.Builder().url(endpoint).headers(headers).get().build()
            }
            EndpointMethod.POST -> {
                Request.Builder().url(endpoint).headers(headers).post(body!!).build()
            }
            EndpointMethod.DELETE -> {
                Request.Builder().url(endpoint).headers(headers).delete().build()
            }
        }
    }
}