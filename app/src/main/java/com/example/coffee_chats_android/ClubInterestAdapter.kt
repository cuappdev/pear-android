package com.example.coffee_chats_android

import android.content.Context
import android.graphics.PorterDuff
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.coffee_chats_android.Models.ClubOrInterest


class ClubInterestAdapter(private val mContext: Context, list: Array<ClubOrInterest>) :
    ArrayAdapter<ClubOrInterest?>(mContext, 0, list) {
    private var clubInterestList = list
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var listItem = convertView
        if (listItem == null) listItem =
            LayoutInflater.from(mContext).inflate(R.layout.club_or_interest_view, parent, false)
        val currentClubInterest = clubInterestList[position]
        val clubOrInterestText = listItem!!.findViewById<TextView>(R.id.club_or_interest_text)
        clubOrInterestText.setText(currentClubInterest.getText())
        val clubOrInterestSubtext = listItem!!.findViewById<TextView>(R.id.club_or_interest_subtext)
        clubOrInterestSubtext.setText(currentClubInterest.getSubtext())

        val selected = context.resources.getColor(R.color.selected_interest_or_club)
        val unselected = context.resources.getColor(R.color.unselected_interest_or_club)
        val layout = listItem!!.findViewById<ConstraintLayout>(R.id.club_or_interest_box)
        val drawableBox = layout.background
        if (currentClubInterest.isSelected()) drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
        else drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)

        return listItem
    }

    init {
        clubInterestList = list
    }
}