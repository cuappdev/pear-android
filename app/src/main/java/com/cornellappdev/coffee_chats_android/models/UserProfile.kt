package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Portions of a user's profile that users can edit */
@JsonClass(generateAdapter = true)
data class UserProfile(
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName: String,
    val hometown: String?,
    @Json(name = "profile_pic_url")
    val profilePicUrl: String?,
    val majors: List<Major>,
    @Json(name = "graduation_year")
    val graduationYear: String?,
    val pronouns: String?,
    val prompts: List<Prompt>,
    val interests: List<Interest>,
    val groups: List<Group>,
    val purposes: List<Purpose>
) {
    companion object {
        val DUMMY_USER_PROFILE = UserProfile(
            firstName = "Dummy",
            lastName = "User",
            hometown = "Vale",
            profilePicUrl = null,
            majors = ArrayList(),
            graduationYear = "2023",
            pronouns = null,
            prompts = ArrayList(),
            purposes = ArrayList(),
            interests = ArrayList(),
            groups = ArrayList()
        )
    }
}