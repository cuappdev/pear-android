package com.cornellappdev.coffee_chats_android

import android.content.Context
import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View
import android.widget.Toast
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.networking.Endpoint
import com.cornellappdev.coffee_chats_android.networking.Request
import com.cornellappdev.coffee_chats_android.networking.updateGroups
import com.cornellappdev.coffee_chats_android.networking.updateInterests
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Helper file for methods or interfaces used across multiple activities or fragments
 */

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
 * Typically implemented by activities when child fragments contain fields for users to fill out
 */
interface OnFilledOutListener {
    fun onFilledOut()
    fun onSelectionEmpty()
}

/**
 * Typically implemented by fragments in which users enter information
 */
interface OnFilledOutObservable {
    fun setOnFilledOutListener(callback: OnFilledOutListener)
    fun saveInformation()
}

/**
 * Makes backend request to update interests or groups with given `items`. Items are interests
 * if `isInterest` is true, and groups otherwise. An error message is displayed via a Toast if an
 * error occurs.
 */
fun updateInterestOrGroup(applicationContext: Context, items: List<String>, isInterest: Boolean) {
    CoroutineScope(Dispatchers.Main).launch {
        val updateEndpoint = if (isInterest) Endpoint.updateInterests(items) else Endpoint.updateGroups(items)
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
