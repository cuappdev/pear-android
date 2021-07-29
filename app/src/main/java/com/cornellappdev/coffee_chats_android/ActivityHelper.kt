package com.cornellappdev.coffee_chats_android

import android.content.Context
import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View
import android.widget.Toast
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.UserField.Category
import com.cornellappdev.coffee_chats_android.networking.*
import com.google.gson.reflect.TypeToken
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
    /** Passes the observable the listener needs to notify when status of fields changes */
    fun setOnFilledOutListener(callback: OnFilledOutListener)
    /** Saves user-entered information in the current fragment on the backend */
    fun saveInformation()
}

/**
 * Makes backend request to update interests or groups with given `items`. Items are interests
 * if `isInterest` is true, and groups otherwise. An error message is displayed via a Toast if an
 * error occurs.
 */
fun updateUserField(applicationContext: Context, items: List<String>, category: Category) {
    CoroutineScope(Dispatchers.Main).launch {
        val updateEndpoint = when (category) {
            Category.INTEREST -> Endpoint.updateInterests(items)
            Category.GROUP -> Endpoint.updateGroups(items)
            Category.GOAL -> Endpoint.updateGoals(items)
            Category.TALKING_POINT -> Endpoint.updateTalkingPoints(items)
        }
        val typeToken = object : TypeToken<ApiResponse<String>>() {}.type
        val updateResponse = withContext(Dispatchers.IO) {
            Request.makeRequest<ApiResponse<String>>(
                updateEndpoint.okHttpRequest(),
                typeToken
            )
        }
        if (updateResponse == null || !updateResponse.success) {
            Toast.makeText(applicationContext, "Failed to save information", Toast.LENGTH_LONG)
                .show()
        }
    }
}
