package com.cornellappdev.coffee_chats_android.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioButton
import com.cornellappdev.coffee_chats_android.R
import kotlinx.android.synthetic.main.pause_pear_popup.view.*


class PopupManager(
    private val c: Context,
    private val popup: PopupWindow,
    state: PopupState
) {
    private var v: View = popup.contentView
    private var radioButtons: Array<RadioButton> =
        arrayOf(v.radio_button_1, v.radio_button_2, v.radio_button_3, v.radio_button_4)

    init {
        v.dismiss_button.setOnClickListener {
            popup.dismiss()
        }
        updatePopupView(state)
    }

    enum class PopupState {
        // user picks how long to pause
        PAUSE,

        // prompts user to start feedback flow
        PROMPT_FEEDBACK,

        // accepts user feedback
        FEEDBACK,

        // allows paused users to unpause
        UNPAUSE
    }

    /**
     * Sets up popup UI based on the given state
     */
    private fun updatePopupView(state: PopupState) {
        when (state) {
            PopupState.PAUSE -> {
                setUpRadioPopup(133, R.array.pause_pear_durations)
            }
            PopupState.PROMPT_FEEDBACK -> {

            }
            PopupState.FEEDBACK -> {
                setUpRadioPopup(160, R.array.pause_pear_reasons)
            }
            PopupState.UNPAUSE -> {

            }
        }
        v.action_button.setOnClickListener {
            when (state) {
                PopupState.PAUSE -> {
                    // TODO make pause backend call
                    updatePopupView(PopupState.PROMPT_FEEDBACK)
                }
                PopupState.PROMPT_FEEDBACK -> {
                    updatePopupView(PopupState.FEEDBACK)
                }
                PopupState.FEEDBACK -> {
                    popup.dismiss()
                }
                PopupState.UNPAUSE -> {
                    // TODO make unpause backend call
                    popup.dismiss()
                }
            }
        }
    }

    /**
     * Sets up a popup UI with radio buttons
     * @param radioGroupWidth Width of radio group
     * @param buttonsStringArrayId Resource id of string array containing radio button labels
     */
    private fun setUpRadioPopup(radioGroupWidth: Int, buttonsStringArrayId: Int) {
        v.popup_title.visibility = View.VISIBLE
        v.radio_group.visibility = View.VISIBLE
        v.radio_group.layoutParams.width = dpToPixels(radioGroupWidth)
        v.action_button.isEnabled = false
        clearRadioButtons()
        val buttonLabels = c.resources.getStringArray(buttonsStringArrayId)
        for (i in radioButtons.indices) {
            radioButtons[i].text = buttonLabels[i]
        }
        v.dismiss_button.text = c.resources.getString(R.string.cancel)
        v.radio_group.setOnCheckedChangeListener { _, _ -> v.action_button.isEnabled = true }
    }

    /** Sets all radio buttons to unchecked */
    private fun clearRadioButtons() {
        for (button in radioButtons) {
            button.isChecked = false
        }
    }

    /** Converts dimension from dp to pixels */
    private fun dpToPixels(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), c.resources.displayMetrics
        ).toInt()
}
