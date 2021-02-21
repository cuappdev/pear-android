package com.cornellappdev.coffee_chats_android.models

class UserSession(
    val accessToken: String,
    val refreshToken: String,
    val sessionExpiration: Long,
    val active: Boolean
)