package com.cornellappdev.coffee_chats_android.singletons

import android.graphics.Bitmap
import android.util.Log
import com.cornellappdev.coffee_chats_android.models.*
import com.cornellappdev.coffee_chats_android.networking.updateDemographics
import com.cornellappdev.coffee_chats_android.networking.updateGroups
import com.cornellappdev.coffee_chats_android.networking.updateInterests
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Integer.max

/**
 * Singleton class for storing user info when editing profile
 */
object UserSingleton {
    const val TAG = "UserSingleton"

    var user = User.DUMMY_USER
        private set

    /** new profile picture set by user, if any */
    var profilePic: Bitmap? = null
        private set

    fun initializeUser(userInfo: User) {
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

    fun addInterest(interest: Interest) {
        val interests = ArrayList<Interest>(user.interests)
        if (interests.contains(interest)) {
            return
        }
        // find insertion index that preserves alphabetical order
        val index = max(interests.indexOfLast { it.name < interest.name } + 1, 0)
        interests.add(index, interest)
        user = user.copy(interests = interests)
    }

    fun addGroup(group: Group) {
        val groups = ArrayList<Group>(user.groups)
        if (groups.contains(group)) {
            return
        }
        // find insertion index that preserves alphabetical order
        val index = max(groups.indexOfLast { it.name < group.name } + 1, 0)
        groups.add(index, group)
        user = user.copy(groups = groups)
    }

    /** Saves all updated user information to backend */
    fun saveUserInfo() {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, user.toString())
            profilePic?.let { com.cornellappdev.coffee_chats_android.networking.updateProfilePic(it) }
            val demographics = Demographics(
                user.firstName,
                user.lastName,
                user.pronouns ?: "",
                user.graduationYear ?: "",
                user.majors.map { it.id },
                user.hometown ?: "",
                null // profilePictureUrl
            )
            updateDemographics(demographics)
            updateInterests(user.interests.map { it.id })
            updateGroups(user.groups.map { it.id })
        }
    }
}