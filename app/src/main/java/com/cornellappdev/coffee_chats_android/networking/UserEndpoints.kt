package com.cornellappdev.coffee_chats_android.networking

import com.cornellappdev.coffee_chats_android.models.UserSession
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

private fun authHeader(): Map<String, String> = mapOf("Authorization" to "Bearer ${UserSession.currentSession.accessToken}")

// AUTH
fun Endpoint.Companion.authenticateUser(idToken: String): Endpoint {
    val codeJSON = JSONObject()
    try {
        codeJSON.put("idToken", idToken)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val requestBody =
        codeJSON.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    return Endpoint(path = "/auth/login", body = requestBody, method = EndpointMethod.POST)
}

fun Endpoint.Companion.refreshSession(refreshToken: String): Endpoint {
    return Endpoint(
        path = "/refresh",
        headers = mapOf("Authorization" to "Bearer $refreshToken"),
        method = EndpointMethod.GET
    )
}

// USER
fun Endpoint.Companion.getUser(netID: String = ""): Endpoint {
    val query = if (netID.isEmpty()) "" else "?netID=$netID"
    return Endpoint(path = "/user/$query", headers = authHeader(), method = EndpointMethod.GET)
}

// ONBOARDING
fun Endpoint.Companion.getAllMajors(): Endpoint {
    return Endpoint(path = "/major/all", headers = authHeader(), method = EndpointMethod.GET)
}