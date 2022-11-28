package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Purpose(
    val id: Int,
    val name: String
)