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

    fun clearLogin() {
        preferences.edit()
            .remove(ACCESS_TOKEN)
            .apply()
    }

    companion object {
        private const val ACCESS_TOKEN = "data.source.prefs.ACCESS_TOKEN"
    }
}