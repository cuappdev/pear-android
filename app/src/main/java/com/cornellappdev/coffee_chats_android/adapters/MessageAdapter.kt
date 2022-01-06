package com.cornellappdev.coffee_chats_android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.models.MatchedUser
import kotlinx.android.synthetic.main.message_cell.view.*

class MessageAdapter(private val matches: List<MatchedUser>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val pearProfileImageView: ImageView = view.pearProfileImageView
        val pearName: TextView = view.pearName
        val currentPearView: View = view.currentPear
        val weekStamp: TextView = view.weekStamp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.message_cell, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = matches[position]
        val context = holder.itemView.context
        Glide.with(context)
            .load(match.profilePicUrl)
            .centerInside()
            .circleCrop()
            .into(holder.pearProfileImageView)
        holder.pearName.text = context.getString(R.string.user_name, match.firstName, match.lastName)
        if (position == 0) {
            holder.currentPearView.visibility = View.VISIBLE
        } else {
            holder.weekStamp.text = context.getString(R.string.week_stamp, position + 1)
        }
    }

    override fun getItemCount(): Int {
        return matches.size
    }
}