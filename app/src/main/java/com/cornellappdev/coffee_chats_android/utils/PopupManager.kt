package com.cornellappdev.coffee_chats_android.utils

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioButton
import com.cornellappdev.coffee_chats_android.OnPauseChangedListener
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.networking.updatePauseStatus
import kotlinx.android.synthetic.main.pause_pear_popup.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Encapsulates Pause Pear popup displayed in `ProfileSettingsActivity`
 */
class PopupManager(
    private val c: Context,
    private val popup: PopupWindow,
    private var state: PopupState,
    private val pauseChangedListener: OnPauseChangedListener
) {
    private val v: View = popup.contentView
    private var radioButtons: Array<RadioButton> =
        arrayOf(v.radio_button_1, v.radio_button_2, v.radio_button_3, v.radio_button_4)

    init {
        v.dismiss_button.setOnClickListener {
            popup.dismiss()
        }
        updatePopupView()
    }

    /**
     * Different UI states for popup
     *
     * When modifying order of enums, please also modify `getStringForState`
     */
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
     * Sets up popup UI based on the current state
     */
    private fun updatePopupView() {
        when (state) {
            PopupState.PAUSE -> {
                setUpRadioPopup(133, R.array.pause_pear_durations)
            }
            PopupState.PROMPT_FEEDBACK -> {
                v.pear_icon.visibility = View.VISIBLE
                v.pause_pear_prompt_feedback.visibility = View.VISIBLE
                v.radio_group.visibility = View.GONE
            }
            PopupState.FEEDBACK -> {
                setUpRadioPopup(180, R.array.pause_pear_reasons)
            }
            PopupState.UNPAUSE -> {
                v.pear_icon.visibility = View.GONE
                v.pause_pear_prompt_feedback.visibility = View.GONE
                v.radio_group.visibility = View.GONE
            }
        }

        v.popup_title.text = getStringForState(R.array.pause_pear_titles)
        v.action_button.text = getStringForState(R.array.pause_pear_actions)
        v.dismiss_button.text = getStringForState(R.array.pause_pear_dismiss_actions)

        v.action_button.isEnabled =
            (state in arrayOf(PopupState.UNPAUSE, PopupState.PROMPT_FEEDBACK))
        v.action_button.setOnClickListener {
            when (state) {
                PopupState.PAUSE -> {
                    state = PopupState.PROMPT_FEEDBACK
                    updatePopupView()
                    var checkedId = -1
                    for (i in radioButtons.indices) {
                        if (radioButtons[i].isChecked) {
                            checkedId = i
                            break
                        }
                    }
                    savePauseStatus(true, if (checkedId < 3) checkedId + 1 else null)
                }
                PopupState.PROMPT_FEEDBACK -> {
                    state = PopupState.FEEDBACK
                    updatePopupView()
                }
                PopupState.FEEDBACK -> {
                    popup.dismiss()
                }
                PopupState.UNPAUSE -> {
                    popup.dismiss()
                    savePauseStatus(false)
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
        v.pear_icon.visibility = View.GONE
        v.pause_pear_prompt_feedback.visibility = View.GONE

        v.popup_title.visibility = View.VISIBLE
        v.radio_group.visibility = View.VISIBLE
        v.radio_group.layoutParams.width = dpToPixels(radioGroupWidth)
        v.radio_group.clearCheck()
        val buttonLabels = c.resources.getStringArray(buttonsStringArrayId)
        for (i in radioButtons.indices) {
            radioButtons[i].text = buttonLabels[i]
        }
        v.radio_group.setOnCheckedChangeListener { _, _ ->
            v.action_button.isEnabled = true
        }
    }

    /** Converts dimension from dp to pixels */
    private fun dpToPixels(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), c.resources.displayMetrics
        ).toInt()

    /**
     * Returns string corresponding to the current state
     *
     * Requires: `i`th entry of the array corresponding to `stringArrayId` is the string corresponding to the `i`th `PopupState`
     */
    private fun getStringForState(stringArrayId: Int): String {
        val stringArr = c.resources.getStringArray(stringArrayId)
        return stringArr[state.ordinal]
    }

    /**
     * Notifies backend and listeners of change in pause status
     */
    private fun savePauseStatus(isPaused: Boolean, pauseWeeks: Int? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            updatePauseStatus(isPaused, pauseWeeks)
            pauseChangedListener.onPauseChanged(isPaused)
        }
    }
}
