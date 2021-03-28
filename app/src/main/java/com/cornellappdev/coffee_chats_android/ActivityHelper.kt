package com.cornellappdev.coffee_chats_android

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View

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