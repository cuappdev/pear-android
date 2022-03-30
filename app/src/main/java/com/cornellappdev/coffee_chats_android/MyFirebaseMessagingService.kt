package com.cornellappdev.coffee_chats_android

import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {

}

//FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//    if (!task.isSuccessful) {
//        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//        return@OnCompleteListener
//    }
//
//    // Get new FCM registration token
//    val token = task.result
//
//    // Log and toast
//    val msg = getString(R.string.msg_token_fmt, token)
//    Log.d(TAG, msg)
//    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//})