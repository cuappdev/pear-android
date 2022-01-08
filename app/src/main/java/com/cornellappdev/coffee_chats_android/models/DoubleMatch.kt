package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DoubleMatch(
    val id: Int,
    val status: String,
    val users: List<MatchedUser>
)
