package com.cornellappdev.coffee_chats_android

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.TouchDelegate
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cornellappdev.coffee_chats_android.models.UserField.Category
import com.cornellappdev.coffee_chats_android.networking.updateGroups
import com.cornellappdev.coffee_chats_android.networking.updateInterests
import com.cornellappdev.coffee_chats_android.networking.updatePurposes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Helper file for methods or interfaces used across multiple activities or fragments
 */

const val ACCESS_TOKEN_TAG = "ACCESS_TOKEN"

/**
 * Increases hit area of `view` on all four sides by given `padding`, which defaults to 100
 */
fun increaseHitArea(view: View, padding: Int = 100) {
    val parent = view.parent as View
    parent.post {
        val rect = Rect()
        view.getHitRect(rect)
        rect.top -= padding
        rect.left -= padding
        rect.bottom += padding
        rect.right += padding
        parent.touchDelegate = TouchDelegate(rect, view)
    }
}

/**
 * Implemented by activities in which fragments contain fields for users to fill out
 */
interface OnFilledOutListener {
    /** Actions to be taken when all required fields are filled out */
    fun onFilledOut()

    /** Actions to be taken when not all required fields are filled out */
    fun onSelectionEmpty()
}

/**
 * Implemented by fragments in which users enter information in fields
 */
interface OnFilledOutObservable {
    /** Passes the observable the listener that it needs to notify when status of fields changes */
    fun setOnFilledOutListener(callback: OnFilledOutListener)

    /** Saves user-entered information in the current fragment on the backend */
    fun saveInformation()
}

/**
 * Implemented by activities that need to be notified when the user's pause status is changed
 */
interface OnPauseChangedListener {
    /** Actions to be taken when the pause status is changed to `isPaused` */
    fun onPauseChanged(isPaused: Boolean)
}

/**
 * Implemented by fragments in which users can change their pause status
 */
interface OnPauseChangedObservable {
    /** Passes the observable the listener that it needs to notify when user's pause status is changed */
    fun setOnPauseChangedListener(callback: OnPauseChangedListener)
}

/**
 * Makes backend request to update interests or groups with given `items`. An error message is
 * displayed via a Toast if an error occurs.
 */
fun updateUserField(applicationContext: Context, items: List<Int>, category: Category) {
    CoroutineScope(Dispatchers.Main).launch {
        val updateResponse = withContext(Dispatchers.IO) {
            when (category) {
                Category.INTEREST -> updateInterests(items)
                Category.GROUP -> updateGroups(items)
                Category.GOAL -> updatePurposes(items)
            }
        }
        if (updateResponse == null || !updateResponse.success) {
            Toast.makeText(applicationContext, "Failed to save information", Toast.LENGTH_LONG)
                .show()
        }
    }
}

/**
 * Hides the keyboard
 */
fun hideKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
}

/** Converts dimension from dp to pixels */
fun dpToPixels(c: Context, dp: Int): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(), c.resources.displayMetrics
    ).toInt()