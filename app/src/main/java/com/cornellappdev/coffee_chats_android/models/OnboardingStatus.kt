package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OnboardingStatus(
    @Json(name = "has_onboarded")
    val hasOnboarded: Boolean
)
