package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfilePicBase64(
    val image: String,
    val bucket: String = "pear"
)
