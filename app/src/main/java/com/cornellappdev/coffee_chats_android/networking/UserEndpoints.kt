package com.cornellappdev.coffee_chats_android.networking

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.cornellappdev.coffee_chats_android.models.*
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type

/* NEW NETWORKING */

private val MEDIA_TYPE = ("application/json; charset=utf-8").toMediaType()

private fun authHeader(): Map<String, String> =
    mapOf("Authorization" to "Token ${UserSession.currentAccessToken}")

private val gson = Gson()

private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

/** Helper for generating request bodies for POST requests */
private fun <T> toRequestBody(data: T, typeToken: Type): RequestBody {
    Log.d("REQUEST_BODY", moshi.adapter<T>(typeToken).toJson(data).toString())
    return moshi.adapter<T>(typeToken).toJson(data)
        .toRequestBody(MEDIA_TYPE)
}

private fun <T> toListRequestBody(key: String, data: List<T>): RequestBody {
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

fun Endpoint.Companion.getAllUsers(query: String): Endpoint {
    val path = if (query.isEmpty()) "/users/" else "/users/?query=$query"
    return Endpoint(path = path, headers = authHeader(), method = EndpointMethod.GET)
}

fun Endpoint.Companion.getUserProfile(userId: Int): Endpoint =
    Endpoint(path = "/users/$userId/", headers = authHeader(), method = EndpointMethod.GET)

fun Endpoint.Companion.updateDemographics(demographics: Demographics): Endpoint {
    val requestBody = toRequestBody(demographics, Demographics::class.java)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

fun bitmapToBase64String(bitmap: Bitmap): String {
    ByteArrayOutputStream().let { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}

fun Endpoint.Companion.updateProfilePic(bitmap: Bitmap): Endpoint {
    val profilePicStr = "data:image/png;base64,${bitmapToBase64String(bitmap)}"
    val requestBody = toRequestBody(ProfilePicBase64(profilePicStr), ProfilePicBase64::class.java)
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

// PROMPTS

fun Endpoint.Companion.getAllPrompts(): Endpoint {
    return Endpoint(path = "/prompts/", headers = authHeader(), method = EndpointMethod.GET)
}

fun Endpoint.Companion.updatePrompts(promptList: List<Prompt>): Endpoint {
    // only send non-empty prompt responses
    val promptResponsesList =
        promptList.filter { it.answer.isNotEmpty() && it.id != -1 }
            .map { Prompt(answer = it.answer, id = it.id) }
    val requestBody = toListRequestBody("prompts", promptResponsesList)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

// PURPOSES

fun Endpoint.Companion.getAllPurposes(): Endpoint {
    return Endpoint(path = "/purposes/", headers = authHeader(), method = EndpointMethod.GET)
}

fun Endpoint.Companion.updatePurposes(purposeIdsList: List<Int>): Endpoint {
    val requestBody = toListRequestBody("purposes", purposeIdsList)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

// SOCIAL MEDIA

fun Endpoint.Companion.updateSocialMedia(socialMedia: SocialMedia): Endpoint {
    val requestBody = toRequestBody(socialMedia, SocialMedia::class.java)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

// ONBOARDING STATUS

fun Endpoint.Companion.updateOnboardingStatus(hasOnboarded: Boolean): Endpoint {
    val requestBody = toRequestBody(OnboardingStatus(hasOnboarded), OnboardingStatus::class.java)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

// EDITING USER PROFILE

fun Endpoint.Companion.updateUserProfile(userProfile: UserProfile): Endpoint {
    // form request body
    val adapter: JsonAdapter<UserProfile> = moshi.adapter(UserProfile::class.java)
    val jsonMap = adapter.toJsonValue(
        userProfile.copy(
            majors = emptyList(),
            interests = emptyList(),
            groups = emptyList(),
            purposes = emptyList()
        )
    )!! as MutableMap<String, Any>
    // some lists have to be converted into ids
    val majorIds = userProfile.majors.map { it.id }
    val interestIds = userProfile.interests.map { it.id }
    val groupIds = userProfile.groups.map { it.id }
    val purposeIds = userProfile.purposes.map { it.id }
    jsonMap["majors"] = majorIds
    jsonMap["interests"] = interestIds
    jsonMap["groups"] = groupIds
    jsonMap["purposes"] = purposeIds
    val requestBody = gson.toJson(jsonMap).toString().toRequestBody(MEDIA_TYPE)

    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

// FCM TOKEN

fun Endpoint.Companion.updateFcmToken(fcmToken: String): Endpoint {
    val requestBody = toRequestBody(FcmToken(fcmToken), FcmToken::class.java)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

// MESSAGE NOTIFICATION

fun Endpoint.Companion.sendMessageNotification(message: String, recipientId: Int): Endpoint {
    val requestBody = toRequestBody(
        Message(message = message),
        Message::class.java
    )
    return Endpoint(
        path = "/users/$recipientId/message/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}

// MATCHES

fun Endpoint.Companion.getCurrentMatch(): Endpoint =
    Endpoint(path = "/matches/current/", headers = authHeader(), method = EndpointMethod.GET)

fun Endpoint.Companion.getSelfMatches(): Endpoint =
    Endpoint(path = "/matches/", headers = authHeader(), method = EndpointMethod.GET)

// PAUSE PEAR

fun Endpoint.Companion.updatePauseStatus(isPaused: Boolean, pauseWeeks: Int?): Endpoint {
    val requestBody = toRequestBody(PauseStatus(isPaused, pauseWeeks), PauseStatus::class.java)
    return Endpoint(
        path = "/me/",
        headers = authHeader(),
        body = requestBody,
        method = EndpointMethod.POST
    )
}


/* OLD NETWORKING */

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