package com.cornellappdev.coffee_chats_android.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.models.ClubOrInterest


class ClubInterestAdapter(private val mContext: Context, list: Array<ClubOrInterest>, club: Boolean) :
    ArrayAdapter<ClubOrInterest?>(mContext, 0, list), Filterable {
    private var clubInterestList = list
    private var isClubView = club
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val viewHolder: ViewHolder
        var listItem: View
        if (convertView == null) {
            if (isClubView) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.club_view, parent, false)
            } else {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.interest_view, parent, false)
            }
            viewHolder = ViewHolder(listItem)
            listItem.tag = viewHolder
        } else {
            listItem = convertView
            viewHolder = listItem.tag as ViewHolder
        }

        val currentClubInterest = clubInterestList[position]
        viewHolder.clubOrInterestText.text = currentClubInterest.getText()
        viewHolder.clubOrInterestSubtext.text = currentClubInterest.getSubtext()

        val selected = context.resources.getColor(R.color.onboardingListSelected)
        val unselected = context.resources.getColor(R.color.onboarding_fields)
        val drawableBox = viewHolder.layout!!.background
        if (currentClubInterest.isSelected()) {
            drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
        } else drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)

        return listItem
    }

    init {
        clubInterestList = list
        isClubView = club
    }

    private class ViewHolder(view: View?) {
        val clubOrInterestText = view?.findViewById(R.id.club_or_interest_text) as TextView
        val clubOrInterestSubtext = view?.findViewById(R.id.club_or_interest_subtext) as TextView
        val layout = view?.findViewById<ConstraintLayout>(R.id.club_or_interest_box)
    }
}