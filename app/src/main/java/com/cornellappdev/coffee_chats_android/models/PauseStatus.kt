package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PauseStatus(
    @Json(name = "is_paused")
    val isPaused: Boolean,
    @Json(name = "pause_weeks")
    val pauseWeeks: Int?
)
