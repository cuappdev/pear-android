package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Prompt(
    val id: Int = -1,
    @Json(name = "question_name")
    val name: String = "",
    @Json(name = "question_placeholder")
    val placeholder: String = "",
    val answer: String = ""
)