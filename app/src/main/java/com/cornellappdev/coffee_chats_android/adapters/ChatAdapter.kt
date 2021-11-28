package com.cornellappdev.coffee_chats_android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.models.Message
import kotlinx.android.synthetic.main.chat_cell.view.*

class ChatAdapter(
    private val messages: List<Message>,
    private val userId: Int,
    private val pearProfilePicUrl: String
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val userChatTextView: TextView = view.userChatTextView
        val pearChatTextView: TextView = view.pearChatTextView
        val pearProfileImageView: ImageView = view.pearProfileImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_cell, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        val isUserMessage = message.senderId == userId
        val textView = if (isUserMessage) holder.userChatTextView else holder.pearChatTextView
        textView.text = message.message
        textView.visibility = View.VISIBLE
        if (!isUserMessage) {
            holder.pearProfileImageView.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(pearProfilePicUrl)
                .centerInside()
                .circleCrop()
                .into(holder.pearProfileImageView)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}