package com.cornellappdev.coffee_chats_android.models

import com.squareup.moshi.Json

data class MatchedUser(
    val id: Int,
    @Json(name = "net_id")
    val netId: String,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName: String,
    @Json(name = "profile_pic_url")
    val profilePicUrl: String?,
    val majors: List<Major>,
    val hometown: String?,
    @Json(name = "graduation_year")
    val graduationYear: String?,
    val pronouns: String?,
    val interests: List<Interest>,
    val groups: List<Group>,
    val prompts: List<Prompt>
)
