package com.cornellappdev.coffee_chats_android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import com.cornellappdev.coffee_chats_android.R
import kotlin.math.floor


class TimeOptionAdapter(
    private val mContext: Context,
    private val list: Array<String>,
    private val selected: MutableList<String>
) :
    ArrayAdapter<String?>(mContext, 0, list), Filterable {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val viewHolder: ViewHolder
        val listItem: View
        if (convertView == null) {
            listItem =
                LayoutInflater.from(mContext).inflate(R.layout.time_option_item, parent, false)
            viewHolder = ViewHolder(listItem)
            listItem.tag = viewHolder
        } else {
            listItem = convertView
            viewHolder = listItem.tag as ViewHolder
        }
        viewHolder.timeTextView!!.text = list[position]
        viewHolder.timeTextView.background = if (selected.contains(list[position])) {
            AppCompatResources.getDrawable(context, R.drawable.selected_rounded_time_option)
        } else {
            AppCompatResources.getDrawable(context, R.drawable.unselected_rounded_time_option)
        }
        return listItem
    }

    private class ViewHolder(view: View?) {
        val timeTextView = view?.findViewById<TextView>(R.id.time_option_text)
    }
}