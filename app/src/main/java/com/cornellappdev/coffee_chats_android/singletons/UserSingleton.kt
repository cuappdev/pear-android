package com.cornellappdev.coffee_chats_android.singletons

import android.graphics.Bitmap
import android.util.Log
import com.cornellappdev.coffee_chats_android.models.*
import com.cornellappdev.coffee_chats_android.networking.updateDemographics
import com.cornellappdev.coffee_chats_android.networking.updateGroups
import com.cornellappdev.coffee_chats_android.networking.updateInterests
import com.cornellappdev.coffee_chats_android.networking.updateUserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Integer.max

/**
 * Singleton class for storing user info when editing profile
 */
object UserSingleton {
    const val TAG = "UserSingleton"

    var user = UserProfile.DUMMY_USER_PROFILE
        private set

    /** new profile picture set by user, if any */
    var profilePic: Bitmap? = null
        private set

    fun initializeUser(userInfo: UserProfile) {
        user = userInfo
    }

    fun updateProfilePic(bitmap: Bitmap) {
        profilePic = bitmap
    }

    fun updateName(name: String) {
        val nameArr = name.split(" ").filter { it.isNotBlank() }
        val n = nameArr.size
        user = when (n) {
            0 -> user.copy(firstName = "", lastName = "")
            1 -> user.copy(firstName = nameArr.first(), lastName = "")
            else -> {
                val lastName = nameArr.takeLast(nameArr.size - 1).joinToString(" ")
                user.copy(firstName = nameArr.first(), lastName = lastName)
            }
        }
    }

    fun updateGraduationYear(graduationYear: String) {
        user = user.copy(graduationYear = graduationYear)
    }

    fun updateMajor(major: Major) {
        user = user.copy(majors = listOf(major))
    }

    fun updateHometown(hometown: String) {
        user = user.copy(hometown = hometown)
    }

    fun updatePronouns(pronouns: String) {
        user = user.copy(pronouns = pronouns)
    }

    fun removeInterest(interestId: Int) {
        val interests = ArrayList<Interest>(user.interests).filter { it.id != interestId }
        user = user.copy(interests = interests)
    }

    fun removeGroup(groupId: Int) {
        val groups = ArrayList<Group>(user.groups).filter { it.id != groupId }
        user = user.copy(groups = groups)
    }

    /** Requires: no interest in `interests` is in userProfile.interests */
    fun addInterests(interests: List<Interest>) {
        val currInterests = ArrayList<Interest>(user.interests)
        // insert interests, preserving alphabetical order
        for (interest in interests) {
            val index = max(currInterests.indexOfLast { it.name < interest.name } + 1, 0)
            currInterests.add(index, interest)
        }
        user = user.copy(interests = currInterests)
    }

    /** Requires: no group in `groups` is in userProfile.groups */
    fun addGroups(groups: List<Group>) {
        val currGroups = ArrayList<Group>(user.groups)
        // insert groups, preserving alphabetical order
        for (group in groups) {
            val index = max(currGroups.indexOfLast { it.name < group.name } + 1, 0)
            currGroups.add(index, group)
        }
        user = user.copy(groups = currGroups)
    }

    /** Saves all updated user information to backend */
    fun saveUserInfo() {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, user.toString())
            profilePic?.let { com.cornellappdev.coffee_chats_android.networking.updateProfilePic(it) }
            updateUserProfile(userProfile = user)
        }
    }
}