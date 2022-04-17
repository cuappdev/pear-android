package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfilePicBase64(
    @Json(name = "profile_pic_base64")
    val image: String
)
