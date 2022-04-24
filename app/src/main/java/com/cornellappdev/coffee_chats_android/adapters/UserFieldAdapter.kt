package com.cornellappdev.coffee_chats_android.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.models.UserField

class UserFieldAdapter(
    private val mContext: Context,
    private val fieldList: List<UserField>,
    private val itemColor: ItemColor,
    private val hideIcon: Boolean = true,
    private val resizeCell: Boolean = false
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

        // changes cell height to wrap content with some padding - currently only used for prompts
        if (resizeCell) {
            listItem.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val padding = context.resources.getDimensionPixelSize(R.dimen.prompts_cell_padding)
            listItem.setPadding(padding, padding, padding, padding)
        }

        val currentClubInterest = fieldList[position]

        viewHolder.clubOrInterestText.text = currentClubInterest.getText()
        if (currentClubInterest.getSubtext().isNotEmpty()) {
            viewHolder.clubOrInterestSubtext.text = currentClubInterest.getSubtext()
        } else {
            viewHolder.clubOrInterestSubtext.visibility = View.GONE
        }

        Glide.with(context)
            .load(currentClubInterest.drawableUrl)
            .into(viewHolder.icon!!)

        if (hideIcon) {
            viewHolder.icon.visibility = View.GONE
        }

        val selected = ContextCompat.getColor(context, R.color.onboardingListSelected)
        val unselected = ContextCompat.getColor(context, R.color.onboarding_fields)
        val drawableBox = viewHolder.layout!!.background
        val greenFilter = PorterDuffColorFilter(selected, PorterDuff.Mode.MULTIPLY)
        val whiteFilter = PorterDuffColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
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
        val icon = view?.findViewById<ImageView>(R.id.rounded_rectangle)
    }
}