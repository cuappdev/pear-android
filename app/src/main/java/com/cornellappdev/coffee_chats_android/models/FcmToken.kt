package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FcmToken(
    @Json(name = "fcm_registration_token")
    val fcmToken: String
)
