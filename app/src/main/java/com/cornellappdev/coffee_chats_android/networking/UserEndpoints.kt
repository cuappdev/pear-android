package com.cornellappdev.coffee_chats_android.networking

import com.cornellappdev.coffee_chats_android.models.*
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

/**
 * Helper for generating GET requests to paths of the form user/`field`/
 */
fun getFieldHelper(netID: String, field: String): Endpoint {
    val query = if (netID.isEmpty()) "" else "?netID=$netID"
    return Endpoint(
        path = "/user/$field/$query",
        headers = authHeader(),
        method = EndpointMethod.GET
    )
}

// USER
fun Endpoint.Companion.getUser(netID: String = ""): Endpoint = getFieldHelper(netID, "")

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

fun Endpoint.Companion.getUserInterests(netID: String = ""): Endpoint =
    getFieldHelper(netID, "interests")

fun Endpoint.Companion.getUserGroups(netID: String = ""): Endpoint = getFieldHelper(netID, "groups")

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

fun Endpoint.Companion.getUserAvailabilities(netID: String = ""): Endpoint =
    getFieldHelper(netID, "availabilities")

fun Endpoint.Companion.updateAvailabilities(availabilities: List<Availability>): Endpoint {
    val json = gson.toJson(mapOf("schedule" to availabilities))
    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    return Endpoint(
        path = "/user/availabilities",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.getUserLocations(netID: String = ""): Endpoint =
    getFieldHelper(netID, "preferredLocations")

fun Endpoint.Companion.updateLocations(locations: List<Location>): Endpoint {
    val json = gson.toJson(mapOf("preferences" to locations))
    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    return Endpoint(
        path = "/user/preferredLocations",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.getUserSocialMedia(netID: String = ""): Endpoint =
    getFieldHelper(netID, "socialMedia")

fun Endpoint.Companion.updateSocialMedia(socialMedia: SocialMedia): Endpoint {
    val requestBody =
        gson.toJson(socialMedia).toRequestBody("application/json; charset=utf-8".toMediaType())
    return Endpoint(
        path = "/user/socialMedia",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.getUserGoals(netID: String = ""): Endpoint = getFieldHelper(netID, "goals")

fun Endpoint.Companion.updateGoals(goals: List<String>): Endpoint {
    val json = gson.toJson(mapOf("goals" to goals))
    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    return Endpoint(
        path = "/user/goals",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun Endpoint.Companion.getUserTalkingPoints(netID: String = ""): Endpoint =
    getFieldHelper(netID, "talkingPoints")

fun Endpoint.Companion.updateTalkingPoints(talkingPoints: List<String>): Endpoint {
    val json = gson.toJson(mapOf("talkingPoints" to talkingPoints))
    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    return Endpoint(
        path = "/user/talkingPoints",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}