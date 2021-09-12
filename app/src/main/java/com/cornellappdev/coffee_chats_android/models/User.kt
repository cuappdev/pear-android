package com.cornellappdev.coffee_chats_android.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
    val profilePictureUrl: String?,
    @Json(name = "facebook_url")
    val facebookUrl: String?,
    @Json(name = "instagram_username")
    val instagramUsername: String?,
    val majors: List<Major>,
    @Json(name = "graduation_year")
    val graduationYear: String?,
    val pronouns: String?,
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
    val currentMatch: User?
)