package com.cornellappdev.coffee_chats_android

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
        var listItem = convertView
        if (isClubView) {
            if (listItem == null) listItem =
                LayoutInflater.from(mContext).inflate(R.layout.club_view, parent, false)
        } else {
            if (listItem == null) listItem =
                LayoutInflater.from(mContext).inflate(R.layout.interest_view, parent, false)
        }
        val currentClubInterest = clubInterestList[position]
        val clubOrInterestText = listItem!!.findViewById<TextView>(R.id.club_or_interest_text)
        clubOrInterestText.setText(currentClubInterest.getText())
        val clubOrInterestSubtext = listItem!!.findViewById<TextView>(R.id.club_or_interest_subtext)
        clubOrInterestSubtext.setText(currentClubInterest.getSubtext())

        val selected = context.resources.getColor(R.color.onboardingListSelected)
        val unselected = context.resources.getColor(R.color.onboarding_fields)
        val layout = listItem!!.findViewById<ConstraintLayout>(R.id.club_or_interest_box)
        val drawableBox = layout.background
        if (currentClubInterest.isSelected()) drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
        else drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)

        return listItem
    }

    init {
        clubInterestList = list
        isClubView = club
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val queryString = p0?.toString()?.toLowerCase()
                val filterResults = Filter.FilterResults()
                filterResults.values =
                    if (queryString == null || queryString.isEmpty()) {
                        clubInterestList
                    } else {
                        clubInterestList.filter {
                            it.getText().toLowerCase().contains(queryString)
                        }.toTypedArray()
                    }
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                clubInterestList = p1!!.values as Array<ClubOrInterest>
                notifyDataSetChanged()
            }

        }
    }
}