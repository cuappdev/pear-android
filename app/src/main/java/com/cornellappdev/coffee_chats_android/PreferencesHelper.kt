package com.cornellappdev.coffee_chats_android

import android.content.Context
import androidx.preference.PreferenceManager

class PreferencesHelper(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var accessToken = preferences.getString(ACCESS_TOKEN, "")
        set(value) {
            preferences.edit().putString(ACCESS_TOKEN, value).apply()
            field = preferences.getString(ACCESS_TOKEN, "")
        }

    var refreshToken = preferences.getString(REFRESH_TOKEN, "")
        set(value) {
            preferences.edit().putString(REFRESH_TOKEN, value).apply()
            field = preferences.getString(REFRESH_TOKEN, "")
        }

    var expiresAt = preferences.getLong(EXPIRES_AT, 0L)
        set(value) {
            preferences.edit().putLong(EXPIRES_AT, value).apply()
            field = preferences.getLong(EXPIRES_AT, 0L)
        }

    var hasOnboarded = preferences.getBoolean(HAS_ONBOARDED, false)
        set(value) {
            preferences.edit().putBoolean(HAS_ONBOARDED, value).apply()
            field = preferences.getBoolean(HAS_ONBOARDED, false)
        }

    fun clearLogin() {
        preferences.edit()
            .remove(ACCESS_TOKEN)
            .remove(REFRESH_TOKEN)
            .remove(EXPIRES_AT)
            .apply()
    }

    companion object {
        private const val ACCESS_TOKEN = "data.source.prefs.ACCESS_TOKEN"
        private const val REFRESH_TOKEN = "data.source.prefs.REFRESH_TOKEN"
        private const val EXPIRES_AT = "data.source.prefs.EXPIRES_AT"
        private const val HAS_ONBOARDED = "data.source.prefs.HAS_ONBOARDED"
    }
}