package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Demographics(
    val pronouns: String,
    @Json(name = "graduation_year")
    val graduationYear: String,
    val majors: List<Int>,
    val hometown: String,
    @Json(name = "profile_pic_url")
    val profilePictureUrl: String?
)