package com.cornellappdev.coffee_chats_android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.models.PearUser
import kotlinx.android.synthetic.main.people_cell.view.*
import kotlinx.android.synthetic.main.people_pill_view.view.*

class PeopleAdapter(private val people: List<PearUser>) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val profileImageView: ImageView = view.profileImageView
        val name: TextView = view.name
        val userInfo: TextView = view.userInfo
        val groupInterestsList: ConstraintLayout = view.group_interests_pill_list
        val groupInterestsFlow: Flow = view.group_interests_pill_flow
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
            // TODO refactor populating pill views into a single function
            val ids = mutableListOf<Int>()
            user.interests.forEach {
                LayoutInflater.from(c).inflate(
                    R.layout.people_pill_view,
                    groupInterestsList,
                    false
                ).apply {
                    id = View.generateViewId()
                    ids.add(id)
                    text_view.text = it.name
                    groupInterestsList.addView(this)
                }
            }
            groupInterestsFlow.referencedIds = ids.toIntArray()
        }
    }

    override fun getItemCount(): Int {
        return people.size
    }
}