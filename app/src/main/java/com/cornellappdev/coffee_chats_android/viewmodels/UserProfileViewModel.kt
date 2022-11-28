package com.cornellappdev.coffee_chats_android.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.coffee_chats_android.models.*
import com.cornellappdev.coffee_chats_android.networking.getUserProfile
import com.cornellappdev.coffee_chats_android.networking.updateDemographics
import com.cornellappdev.coffee_chats_android.networking.updateGroups
import com.cornellappdev.coffee_chats_android.networking.updateInterests
import kotlinx.coroutines.launch
import java.lang.Integer.max

/**
 * ViewModel used in ProfileActivity for storing user info when editing profile
 */
class UserProfileViewModel : ViewModel() {
    lateinit var userProfile : UserProfile
        private set

    /** new profile picture set by user, if any */
    var profilePic: Bitmap? = null
        private set

    init {
        syncUserProfile()
    }

    /** Syncs userProfile with user profile in backend */
    fun syncUserProfile() = viewModelScope.launch {
        Log.d(TAG, "Syncing user profile")
        userProfile = getUserProfile()
    }

    fun updateProfilePic(bitmap: Bitmap) {
        profilePic = bitmap
    }

    fun updateName(name: String) {
        val nameArr = name.split(" ").filter { it.isNotBlank() }
        val n = nameArr.size
        userProfile = when (n) {
            0 -> userProfile.copy(firstName = "", lastName = "")
            1 -> userProfile.copy(firstName = nameArr.first(), lastName = "")
            else -> {
                val lastName = nameArr.takeLast(nameArr.size - 1).joinToString(" ")
                userProfile.copy(firstName = nameArr.first(), lastName = lastName)
            }
        }
    }

    fun updateGraduationYear(graduationYear: String) {
        userProfile = userProfile.copy(graduationYear = graduationYear)
    }

    fun updateMajor(major: Major) {
        userProfile = userProfile.copy(majors = listOf(major))
    }

    fun updateHometown(hometown: String) {
        userProfile = userProfile.copy(hometown = hometown)
    }

    fun updatePronouns(pronouns: String) {
        userProfile = userProfile.copy(pronouns = pronouns)
    }

    fun removeInterest(interestId: Int) {
        val interests = ArrayList<Interest>(userProfile.interests).filter { it.id != interestId }
        userProfile = userProfile.copy(interests = interests)
    }

    fun removeGroup(groupId: Int) {
        val groups = ArrayList<Group>(userProfile.groups).filter { it.id != groupId }
        userProfile = userProfile.copy(groups = groups)
    }

    /** Requires: no interest in `interests` is in userProfile.interests */
    fun addInterests(interests: List<Interest>) {
        val currInterests = ArrayList<Interest>(userProfile.interests)
        // insert interests, preserving alphabetical order
        for (interest in interests) {
            val index = max(currInterests.indexOfLast { it.name < interest.name } + 1, 0)
            currInterests.add(index, interest)
        }
        userProfile = userProfile.copy(interests = currInterests)
    }

    /** Requires: no group in `groups` is in userProfile.groups */
    fun addGroups(groups: List<Group>) {
        val currGroups = ArrayList<Group>(userProfile.groups)
        // insert groups, preserving alphabetical order
        for (group in groups) {
            val index = max(currGroups.indexOfLast { it.name < group.name } + 1, 0)
            currGroups.add(index, group)
        }
        userProfile = userProfile.copy(groups = currGroups)
    }

    /** Saves all updated user information to backend */
    fun saveUserInfo() {
        Log.d(TAG, userProfile.toString())
        if (ENABLE_SAVE_TO_BACKEND) {
            viewModelScope.launch {
                profilePic?.let { com.cornellappdev.coffee_chats_android.networking.updateProfilePic(it) }
                val demographics = Demographics(
                    userProfile.firstName,
                    userProfile.lastName,
                    userProfile.pronouns ?: "",
                    userProfile.graduationYear ?: "",
                    userProfile.majors.map { it.id },
                    userProfile.hometown ?: "",
                    null // profilePictureUrl
                )
                updateDemographics(demographics)
                updateInterests(userProfile.interests.map { it.id })
                updateGroups(userProfile.groups.map { it.id })
            }
        }
    }

    companion object {
        const val TAG = "UserProfileViewModel"
        // TODO: Enable this
        const val ENABLE_SAVE_TO_BACKEND = false
    }
}