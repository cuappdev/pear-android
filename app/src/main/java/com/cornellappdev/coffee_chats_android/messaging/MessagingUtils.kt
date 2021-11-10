package com.cornellappdev.coffee_chats_android.messaging

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

const val TAG = "MessagingUtils"

/**
 * Gets messages between two users with message ids `currUserId` and `otherUserId` from the
 * Firebase Realtime Database
 */
fun getMessages(currUserId: Int, otherUserId: Int) {
    val database = FirebaseDatabase.getInstance()
    val messageIdsRef: DatabaseReference =
        database.getReference("user-messages").child("$currUserId").child("$otherUserId")
    messageIdsRef.get().addOnSuccessListener {
        val messageIds = it.value as HashMap<String, Int>
        messageIds.keys.forEach { key ->
            val messageRef = database.getReference("messages").child(key)
            messageRef.get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "${snapshot.value}")
            }
        }
    }.addOnFailureListener {
        Log.e(TAG, "Error getting data", it)
    }
}