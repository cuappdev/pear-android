package com.cornellappdev.coffee_chats_android.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.ProfileActivity
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.hideKeyboard
import com.cornellappdev.coffee_chats_android.models.PearUser
import kotlinx.android.synthetic.main.people_cell.view.*
import kotlinx.android.synthetic.main.people_pill_view.view.*

/**
 * Adapter for use in PeopleFragment, displaying a list of all users
 */
class PeopleAdapter(private val people: List<PearUser>) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val profileImageView: ImageView = view.profileImageView
        val name: TextView = view.name
        val userInfo: TextView = view.userInfo
        val interestsList: ConstraintLayout = view.interests_pill_list
        val interestsFlow: Flow = view.interests_pill_flow
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.people_cell, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = people[position]
        val c = holder.itemView.context
        holder.apply {
            Glide.with(c)
                .load(user.profilePicUrl)
                .centerInside()
                .circleCrop()
                .into(profileImageView)
            name.text = c.getString(R.string.user_name, user.firstName, user.lastName)
            userInfo.text = c.getString(
                R.string.user_info,
                user.majors.first().name,
                user.graduationYear,
                user.hometown
            )
            // populate pill views
            val ids = mutableListOf<Int>()
            // clear previous pills to prevent UI bugs after scrolling
            val numChildren = interestsList.childCount
            if (numChildren > 1) {
                // start at 1 in order not to remove flow widget
                interestsList.removeViews(1, numChildren - 1)
            }
            user.interests.forEach {
                LayoutInflater.from(c).inflate(
                    R.layout.people_pill_view,
                    interestsList,
                    false
                ).apply {
                    id = View.generateViewId()
                    ids.add(id)
                    text_view.text = it.name
                    interestsList.addView(this)
                }
            }
            interestsFlow.referencedIds = ids.toIntArray()
            // navigate to user profile on click
            itemView.setOnClickListener {
                val intent = Intent(c, ProfileActivity::class.java)
                intent.putExtra(ProfileActivity.USER_ID, user.id)
                hideKeyboard(c, it)
                c.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return people.size
    }
}