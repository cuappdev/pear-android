package com.cornellappdev.coffee_chats_android.models

import java.io.Serializable

class UserProfile(val userName: String, val userEmail: String): Serializable{
    // sign-in info
    private var name = userName
    private var email = userEmail

    // demographics
    private var classOf = 0
    private var major = ""
    private var hometown = ""
    private var pronoun = ""

    // interests and clubs
    private lateinit var interest: Array<String>
    private lateinit var clubs: Array<String>
}