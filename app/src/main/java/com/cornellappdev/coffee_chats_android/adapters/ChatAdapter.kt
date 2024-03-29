package com.cornellappdev.coffee_chats_android.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.ProfileActivity
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.hideKeyboard
import com.cornellappdev.coffee_chats_android.models.Message
import kotlinx.android.synthetic.main.chat_cell.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class ChatAdapter(
    private val messages: List<Message>,
    private val userId: Int,
    private val pearProfilePicUrl: String
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat.getDateInstance()

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val userChatTextView: TextView = view.userChatTextView
        val pearChatTextView: TextView = view.pearChatTextView
        val pearProfileImageView: ImageView = view.pearProfileImageView
        val dateStamp: TextView = view.dateStamp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_cell, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        // display message
        val isUserMessage = message.senderId == userId
        val textView = if (isUserMessage) holder.userChatTextView else holder.pearChatTextView
        // hide other textview
        if (isUserMessage) {
            holder.pearChatTextView.visibility = View.GONE
        } else {
            holder.userChatTextView.visibility = View.GONE
        }
        textView.text = message.message
        textView.visibility = View.VISIBLE
        // display profile picture as needed
        if (!isUserMessage) {
            val c = holder.itemView.context
            holder.pearProfileImageView.visibility = View.VISIBLE
            Glide.with(c)
                .load(pearProfilePicUrl)
                .centerInside()
                .circleCrop()
                .into(holder.pearProfileImageView)
            holder.pearProfileImageView.setOnClickListener {
                Intent(c, ProfileActivity::class.java).apply {
                    putExtra(ProfileActivity.USER_ID, message.senderId)
                    c.startActivity(this)
                }
            }
        } else {
            holder.pearProfileImageView.visibility = View.GONE
        }
        // display date as needed
        val currDate = getFormattedDate(messages[position])
        if (position == 0 || getFormattedDate(messages[position - 1]) != currDate) {
            holder.dateStamp.visibility = View.VISIBLE
            holder.dateStamp.text = currDate
        } else {
            holder.dateStamp.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            hideKeyboard(holder.itemView.context, holder.itemView)
        }
    }

    private fun getFormattedDate(message: Message): String {
        val timestamp = message.time.toDouble().roundToLong()
        calendar.timeInMillis = timestamp * 1000L
        return formatter.format(calendar.time)
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}