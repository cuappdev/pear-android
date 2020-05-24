package com.cornellappdev.coffee_chats_android.models

import java.io.Serializable

class UserProfile(val userName: String, val userEmail: String): Serializable{
    // sign-in info
    var name = userName
    var email = userEmail

    // demographics
    var classOf = 0
    var major = ""
    var hometown = ""
    var pronoun = ""

    // interests and clubs
    var interests = arrayListOf<String>()
    var clubs = arrayListOf<String>()

    // scheduling
    /*
     * [availableTimes] maps from day (acronym) to a list of times (in the format of strings as displayed)
     * e.g. if the user is available on Sunday at 3:00 and 8:00, then the HashMap would be:
     * "Su" -> ["3:00", "8:00"]
     */
    var availableTimes = HashMap<String, MutableList<String>>()
    /*
     * [preferredLocations] includes a list of selected locations in strings as displayed
     * e.g. ["Atrium Cafe", "Martha's Cafe", "Kung Fu Tea"]
     */
    var preferredLocations = mutableListOf<String>()
}