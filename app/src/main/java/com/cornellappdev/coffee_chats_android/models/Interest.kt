package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Interest(
    val id: Int,
    val name: String,
    val subtitle: String,
    @Json(name = "img_url")
    val imageUrl: String = ""
)
