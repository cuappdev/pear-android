package com.cornellappdev.coffee_chats_android.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserSession(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "username")
    val netId: String,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName: String
) {
    companion object {
        lateinit var currentAccessToken: String
    }
}