package com.cornellappdev.coffee_chats_android.singletons

import com.cornellappdev.coffee_chats_android.models.Group
import com.cornellappdev.coffee_chats_android.models.Interest
import com.cornellappdev.coffee_chats_android.models.User
import java.lang.Integer.max

/**
 * Singleton class for storing user info when editing profile
 */
object UserSingleton {
    var user = User.DUMMY_USER
        private set

    fun initializeUser(userInfo: User) {
        user = userInfo
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

    /** Saves user information to backend */
    fun saveUserInfo() {
        TODO()
    }
}