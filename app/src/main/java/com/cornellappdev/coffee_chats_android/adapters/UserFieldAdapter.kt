package com.cornellappdev.coffee_chats_android.adapters

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.models.UserField


class UserFieldAdapter(
    private val mContext: Context,
    private val fieldList: List<UserField>,
    private val itemColor: ItemColor
) :
    ArrayAdapter<UserField?>(mContext, 0, fieldList), Filterable {

    enum class ItemColor {
        WHITE,
        GREEN,
        TOGGLE
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val viewHolder: ViewHolder
        val listItem: View
        if (convertView == null) {
            listItem =
                LayoutInflater.from(mContext).inflate(R.layout.interest_view, parent, false)
            viewHolder = ViewHolder(listItem)
            listItem.tag = viewHolder
        } else {
            listItem = convertView
            viewHolder = listItem.tag as ViewHolder
        }

        val currentClubInterest = fieldList[position]
        viewHolder.clubOrInterestText.text = currentClubInterest.getText()
        viewHolder.clubOrInterestSubtext.text = currentClubInterest.getSubtext()

        val selected = ContextCompat.getColor(context, R.color.onboardingListSelected)
        val unselected = ContextCompat.getColor(context, R.color.onboarding_fields)
        val drawableBox = viewHolder.layout!!.background
        val greenFilter = BlendModeColorFilter(selected, BlendMode.MULTIPLY)
        val whiteFilter = BlendModeColorFilter(unselected, BlendMode.MULTIPLY)
        drawableBox.colorFilter = when (itemColor) {
            ItemColor.WHITE -> whiteFilter
            ItemColor.GREEN -> greenFilter
            ItemColor.TOGGLE -> if (currentClubInterest.isSelected()) greenFilter else whiteFilter
        }
        return listItem
    }

    private class ViewHolder(view: View?) {
        val clubOrInterestText = view?.findViewById(R.id.group_or_interest_text) as TextView
        val clubOrInterestSubtext = view?.findViewById(R.id.group_or_interest_subtext) as TextView
        val layout = view?.findViewById<ConstraintLayout>(R.id.group_or_interest_box)
    }
}