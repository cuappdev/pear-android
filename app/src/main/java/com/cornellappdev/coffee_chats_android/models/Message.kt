package com.cornellappdev.coffee_chats_android.models

data class Message(val senderId: Int = -1, val recipientId: Int = -1, val time: String = "", val message: String = "") {
    /**
     * Converts data to a map of keys to values
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "senderId" to senderId,
            "recipientId" to recipientId,
            "time" to time,
            "message" to message
        )
    }
}