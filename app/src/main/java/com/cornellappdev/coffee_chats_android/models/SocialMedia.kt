package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocialMedia(
    @Json(name = "facebook_url")
    val facebookUrl: String?,
    @Json(name = "instagram_username")
    val instagramUsername: String?
)