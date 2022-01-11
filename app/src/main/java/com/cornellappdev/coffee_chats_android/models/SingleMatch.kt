package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json

data class SingleMatch(
    val id: Int,
    val status: String,
    @Json(name = "matched_user")
    val matchedUser: PearUser
)
