package com.cornellappdev.coffee_chats_android.models

class ClubOrInterest {
    var text: String
        private set
    var subtext: String
        private set

    constructor(text: String, subtext: String) {
        this.text = text
        this.subtext = subtext
    }
}