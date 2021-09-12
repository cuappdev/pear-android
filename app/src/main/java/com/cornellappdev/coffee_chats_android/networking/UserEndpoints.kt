package com.cornellappdev.coffee_chats_android.networking

import android.util.Log
import com.cornellappdev.coffee_chats_android.models.*
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

/* NEW NETWORKING */

private val MEDIA_TYPE = ("application/json; charset=utf-8").toMediaType()

private fun authHeader(): Map<String, String> =
    mapOf("Authorization" to "Token ${UserSession.currentAccessToken}")

private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

/** Helper for generating request bodies for POST requests */
private fun <T> toRequestBody(data: T, typeToken: Type): RequestBody {
    Log.d("REQUEST_BODY", moshi.adapter<T>(typeToken).toJson(data).toString())
    return moshi.adapter<T>(typeToken).toJson(data)
        .toRequestBody(MEDIA_TYPE)
}

private fun toListRequestBody(key: String, data: List<Int>): RequestBody {
    val json = gson.toJson(mapOf(key to data))
    return json.toString().toRequestBody(MEDIA_TYPE)
}

// AUTH
fun Endpoint.Companion.authenticateUser(idToken: String): Endpoint =
    Endpoint(
        path = "/authenticate/",
        body = mapToRequestBody(mapOf("id_token" to idToken)),
        method = EndpointMethod.POST
    )

// PROFILE

fun Endpoint.Companion.getSelfProfile(): Endpoint =
    Endpoint(path = "/me/", headers = authHeader(), method = EndpointMethod.GET)

fun Endpoint.Companion.updateDemographics(demographics: Demographics): Endpoint {
    val requestBody = toRequestBody(demographics, Demographics::class.java)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

// MAJORS

fun Endpoint.Companion.getAllMajors(): Endpoint {
    return Endpoint(path = "/majors/", headers = authHeader(), method = EndpointMethod.GET)
}

// INTERESTS

fun Endpoint.Companion.getAllInterests(): Endpoint {
    return Endpoint(path = "/interests/", headers = authHeader(), method = EndpointMethod.GET)
}

fun Endpoint.Companion.updateInterests(interestIdsList: List<Int>): Endpoint {
    val requestBody = toListRequestBody("interests", interestIdsList)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

// GROUPS

fun Endpoint.Companion.getAllGroups(): Endpoint {
    return Endpoint(path = "/groups/", headers = authHeader(), method = EndpointMethod.GET)
}

fun Endpoint.Companion.updateGroups(groupIdsList: List<Int>): Endpoint {
    val requestBody = toListRequestBody("groups", groupIdsList)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

/* OLD NETWORKING */

private val gson = Gson()

private fun <K, V> mapToRequestBody(map: Map<K, V>): RequestBody =
    gson.toJson(map).toRequestBody("application/json; charset=utf-8".toMediaType())

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

fun Endpoint.Companion.getUserInterests(netID: String = ""): Endpoint =
    getFieldHelper(netID, "interests")

fun Endpoint.Companion.getUserGroups(netID: String = ""): Endpoint = getFieldHelper(netID, "groups")

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