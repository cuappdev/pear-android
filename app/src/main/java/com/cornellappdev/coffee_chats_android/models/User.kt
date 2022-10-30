package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Current user's profile info. More detailed than `PearUser`.
 */
@JsonClass(generateAdapter = true)
data class User(
    val id: Int,
    @Json(name = "net_id")
    val netId: String,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName: String,
    val hometown: String?,
    @Json(name = "profile_pic_url")
    val profilePicUrl: String?,
    @Json(name = "facebook_url")
    val facebookUrl: String?,
    @Json(name = "instagram_username")
    val instagramUsername: String?,
    val majors: List<Major>,
    @Json(name = "graduation_year")
    val graduationYear: String?,
    val pronouns: String?,
    val prompts: List<Prompt>,
    val purposes: List<Purpose>,
    val goals: List<String>?,
    @Json(name = "talking_points")
    val talkingPoints: List<String>?,
    val availability: List<String>?,
    val locations: List<String>,
    val interests: List<Interest>,
    val groups: List<Group>,
    @Json(name = "has_onboarded")
    val hasOnboarded: Boolean,
    @Json(name = "pending_feedback")
    val pendingFeedback: Boolean,
    @Json(name = "current_match")
    val currentMatch: SingleMatch?,
    @Json(name = "is_paused")
    val isPaused: Boolean,
    @Json(name = "pause_expiration")
    val pauseExpiration: String?
) {
    companion object {
        val DUMMY_USER = User(
            id = -1,
            netId = "dummy",
            firstName = "Dummy",
            lastName = "User",
            hometown = "Vale",
            profilePicUrl = null,
            facebookUrl = null,
            instagramUsername = null,
            majors = ArrayList(),
            graduationYear = "2023",
            pronouns = null,
            prompts = ArrayList(),
            purposes = ArrayList(),
            goals = ArrayList(),
            talkingPoints = null,
            availability = null,
            locations = ArrayList(),
            interests = ArrayList(),
            groups = ArrayList(),
            hasOnboarded = true,
            pendingFeedback = false,
            currentMatch = null,
            isPaused = false,
            pauseExpiration = null,
        )
    }
}