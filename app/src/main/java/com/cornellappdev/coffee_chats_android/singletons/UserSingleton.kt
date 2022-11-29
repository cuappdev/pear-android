package com.cornellappdev.coffee_chats_android.singletons

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.cornellappdev.coffee_chats_android.models.*
import com.cornellappdev.coffee_chats_android.networking.*
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

    var promptsArray = Array(3) { UserField() }
        private set

    /** Syncs user profile with backend */
    fun syncUser() {
        profilePic = null
        CoroutineScope(Dispatchers.Main).launch {
            user = getUserProfile()
            promptsArray = Array(3) { UserField() }
            user.prompts.map { p ->
                UserField(
                    text = p.name,
                    subtext = p.answer,
                    id = p.id
                )
            }.toTypedArray().copyInto(promptsArray)
        }
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

    fun updatePrompt(pos: Int, prompt: String, response: String, promptId: Int) {
        promptsArray[pos] = UserField(
            text = prompt,
            subtext = response,
            id = promptId
        )
    }

    fun removePrompt(pos: Int) {
        promptsArray[pos] = UserField()
    }

    /** Saves all updated user information to backend */
    fun saveUserInfo() {
        CoroutineScope(Dispatchers.Main).launch {
            val prompts = promptsArray.toList()
                .map { Prompt(answer = it.getSubtext(), id = it.id) }
                .filter { it.answer.isNotBlank() }
            user = user.copy(prompts = prompts)
            Log.d(TAG, user.toString())
            profilePic?.let { com.cornellappdev.coffee_chats_android.networking.updateProfilePic(it) }
            updateUserProfile(userProfile = user)
        }
    }
}