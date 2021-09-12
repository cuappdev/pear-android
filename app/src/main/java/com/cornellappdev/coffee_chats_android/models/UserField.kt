package com.cornellappdev.coffee_chats_android.models

class UserField(private var text: String = "", private var subtext: String = "", val drawableId: Int? = null, val id: Int = -1) {
    enum class Category {
        INTEREST,
        GROUP,
        GOAL
    }

    private var selected: Boolean = false

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