package com.cornellappdev.coffee_chats_android.models

class UserSession(
    val accessToken: String,
    val refreshToken: String,
    val sessionExpiration: String,
    val active: Boolean
) {
    companion object {
        lateinit var currentSession: UserSession
    }
}