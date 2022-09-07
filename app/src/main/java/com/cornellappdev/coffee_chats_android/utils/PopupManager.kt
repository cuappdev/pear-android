package com.cornellappdev.coffee_chats_android.utils

import android.content.Context
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.TextView
import com.cornellappdev.coffee_chats_android.R

class PopupManager(
    private val c: Context,
    popup: PopupWindow,
    state: PopupState
) {
    var popupTitle: TextView
    var radio1: RadioButton
    var radio2: RadioButton
    var radio3: RadioButton
    var radio4: RadioButton

    init {
        val v = popup.contentView
        popupTitle = v.findViewById(R.id.popup_title)
        radio1 = v.findViewById(R.id.radio_button_1)
        radio2 = v.findViewById(R.id.radio_button_2)
        radio3 = v.findViewById(R.id.radio_button_3)
        radio4 = v.findViewById(R.id.radio_button_4)
        updatePopupView(state)
    }

    enum class PopupState {
        // user picks how long to pause
        PAUSE_DURATION,

        // prompts user to start feedback flow
        PROMPT_FEEDBACK,

        // accepts user feedback
        FEEDBACK,

        // user already paused
        PAUSED
    }

    private fun updatePopupView(state: PopupState) {
        when (state) {
            PopupState.PAUSE_DURATION -> {
                popupTitle.visibility = View.VISIBLE
                radio1.text = c.resources.getQuantityString(R.plurals.number_of_weeks, 1, 1)
                radio2.text = c.resources.getQuantityString(R.plurals.number_of_weeks, 2, 2)
                radio3.text = c.resources.getQuantityString(R.plurals.number_of_weeks, 3, 3)
                radio4.text = c.getString(R.string.indefinitely)
            }
            else -> throw Error("Unimplemented")
        }
    }
}
