package com.cornellappdev.coffee_chats_android.networking

import com.cornellappdev.coffee_chats_android.models.Demographics
import com.cornellappdev.coffee_chats_android.models.UserSession
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private fun authHeader(): Map<String, String> =
    mapOf("Authorization" to "Bearer ${UserSession.currentSession.accessToken}")

private val gson = Gson()

// AUTH
fun Endpoint.Companion.authenticateUser(idToken: String): Endpoint {
    val json = gson.toJson(mapOf("idToken" to idToken))
    val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
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

fun Endpoint.Companion.updateDemographics(demographics: Demographics): Endpoint {
    val requestBody =
        gson.toJson(demographics).toRequestBody("application/json; charset=utf-8".toMediaType())
    return Endpoint(
        path = "/user/demographics",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.getAllInterests(): Endpoint {
    // TODO: To be integrated once both titles and subtitles are provided
    return Endpoint(path = "/interest/all", headers = authHeader(), method = EndpointMethod.GET)
}

fun Endpoint.Companion.getAllGroups(): Endpoint {
    return Endpoint(path = "/group/all", headers = authHeader(), method = EndpointMethod.GET)
}

fun Endpoint.Companion.getUserInterests(netID: String = ""): Endpoint {
    val query = if (netID.isEmpty()) "" else "?netID=$netID"
    return Endpoint(
        path = "/user/interests/$query",
        headers = authHeader(),
        method = EndpointMethod.GET
    )
}

fun Endpoint.Companion.getUserGroups(netID: String = ""): Endpoint {
    val query = if (netID.isEmpty()) "" else "?netID=$netID"
    return Endpoint(
        path = "/user/groups/$query",
        headers = authHeader(),
        method = EndpointMethod.GET
    )
}

fun Endpoint.Companion.updateInterests(interests: List<String>): Endpoint {
    val json = gson.toJson(mapOf("interests" to interests))
    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    return Endpoint(
        path = "/user/interests",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.updateGroups(groups: List<String>): Endpoint {
    val json = gson.toJson(mapOf("groups" to groups))
    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    return Endpoint(
        path = "/user/groups",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}