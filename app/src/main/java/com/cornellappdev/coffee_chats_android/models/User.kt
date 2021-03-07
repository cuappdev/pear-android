package com.cornellappdev.coffee_chats_android.models

data class User(
    val netID: String,
    val firstName: String,
    val lastName: String,
    val hometown: String?,
    val profilePictureURL: String?,
    val major: String?,
    val graduationYear: String?,
    val pronouns: String?,
    val interests: List<String>,
    val groups: List<String>
)