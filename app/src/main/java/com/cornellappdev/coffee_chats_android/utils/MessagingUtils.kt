package com.cornellappdev.coffee_chats_android.utils

import android.util.Log
import com.cornellappdev.coffee_chats_android.models.Message
import com.cornellappdev.coffee_chats_android.networking.sendMessageNotification
import com.cornellappdev.coffee_chats_android.networking.updateFcmToken
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Util methods for working with messages via the Firebase Realtime Database
 */

const val TAG = "MessagingUtils"

val database = FirebaseDatabase.getInstance().reference

/**
 * Gets messages between two users with message ids `currUserId` and `otherUserId` and adds a
 * listener that notifies observer of new messages
 */
fun getMessages(currUserId: Int, otherUserId: Int, observer: MessageObserver) {
    val messageEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val messageId = snapshot.key
            val messageRef = database.child("messages/$messageId")
            messageRef.get().addOnSuccessListener {
                observer.onMessageReceived(it.getValue<Message>() as Message)
            }.addOnFailureListener {
                Log.e(TAG, "Error getting new message", it)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }
    database.child("user-messages/$currUserId/$otherUserId")
        .addChildEventListener(messageEventListener)
}

/**
 * Sends message `message` from user with id `currUserId` to user with id `otherUserId`
 */
fun sendMessage(message: String, currUserId: Int, otherUserId: Int, observer: MessageObserver) {
    // generate new key for the message
    val key = database.child("messages").push().key!!

    // using 7 digits of precision for parity
    val time = "%.7f".format((System.currentTimeMillis().toDouble() / 1000))
    val messageValues = Message(currUserId, otherUserId, time, message).toMap()

    // update messages database, and let each user know of the new message
    val childUpdates = hashMapOf<String, Any>(
        "messages/$key" to messageValues,
        "user-messages/$currUserId/$otherUserId/$key" to 1,
        "user-messages/$otherUserId/$currUserId/$key" to 1
    )
    database.updateChildren(childUpdates)
        .addOnCanceledListener { observer.onMessageSendFailed() }

    CoroutineScope(Dispatchers.Main).launch {
        sendMessageNotification(message, otherUserId)
    }
}

interface MessageObserver {
    /**
     * Notifies observers that a message was received
     */
    fun onMessageReceived(message: Message)

    /**
     * Notifies observers that an attempt to send a message failed
     */
    fun onMessageSendFailed()
}