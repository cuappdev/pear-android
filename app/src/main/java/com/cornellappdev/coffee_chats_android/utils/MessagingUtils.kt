package com.cornellappdev.coffee_chats_android.utils

import android.util.Log
import com.cornellappdev.coffee_chats_android.models.Message
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

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
    val messageIdsRef: DatabaseReference = database.child("user-messages/$currUserId/$otherUserId")
    messageIdsRef.get().addOnSuccessListener {
        val messages = it.value as Map<String, Int>
        val messageIds = messages.keys
        for (id in messageIds) {
            Log.d(TAG, "Message id: $id")
            val messageRef = database.child("messages/$id")
            messageRef.get().addOnSuccessListener { snapshot ->
                observer.onMessageReceived(snapshot.getValue<Message>() as Message)
            }
        }
    }.addOnFailureListener {
        Log.e(TAG, "Error getting data", it)
    }
}

fun addMessagesListener(currUserId: Int, otherUserId: Int, observer: MessageObserver) {
    val messageEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val message = snapshot.getValue<Message>()!!
            if (message.recipientId == currUserId && message.senderId == otherUserId || message.recipientId == otherUserId && message.senderId == currUserId) {
                observer.onMessageReceived(message)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }
    database.child("messages").addChildEventListener(messageEventListener)
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