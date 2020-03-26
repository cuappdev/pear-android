package com.cornellappdev.coffee_chats_android.models

class ClubOrInterest {
    private var text: String
    private var subtext: String
    private var selected: Boolean

    constructor(text: String, subtext: String) {
        this.text = text
        this.subtext = subtext
        this.selected = false
    }

    fun getText() : String {
        return text
    }

    fun getSubtext() : String {
        return subtext
    }

    fun toggleSelected() {
        selected = !selected
    }

    fun setSelected() {
        selected = true
    }

    fun isSelected() : Boolean {
        return selected
    }
}