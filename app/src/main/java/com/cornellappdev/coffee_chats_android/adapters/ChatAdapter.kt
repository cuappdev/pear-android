package com.cornellappdev.coffee_chats_android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.models.Message

class ChatAdapter(private val messages: List<Message>, private val userId: Int) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val userChatTextView: TextView = view.findViewById(R.id.userChatTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_cell, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        if (message.senderId == userId) {
            holder.userChatTextView.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}