package com.cornellappdev.coffee_chats_android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.cornellappdev.coffee_chats_android.R


class DayAdapter(
    private val mContext: Context,
    private val days: Array<String>,
    private val daysFullName: Array<String>,
    private val selected: MutableSet<String>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val dayView: View

        if (convertView == null) {
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            dayView = inflater.inflate(R.layout.time_scheduling_day_layout, null)
            viewHolder = ViewHolder(dayView)
            dayView.tag = viewHolder
        } else {
            dayView = convertView
            viewHolder = dayView.tag as ViewHolder
        }
        viewHolder.dayTextView.text = days[position]
        if (position == 0) viewHolder.dayDot.visibility = View.VISIBLE
        if (selected.contains(daysFullName[position])) {
            viewHolder.dayTextView.background = AppCompatResources.getDrawable(
                mContext,
                R.drawable.selected_scheduling_circle_button
            )
        } else {
            viewHolder.dayTextView.background = AppCompatResources.getDrawable(
                mContext,
                R.drawable.unselected_scheduling_circle_button
            )
        }
        return dayView
    }

    override fun getItem(position: Int): Any {
        return days[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return days.size
    }

    private class ViewHolder(view: View?) {
        val dayTextView = view?.findViewById(R.id.day_button) as TextView
        val dayDot = view?.findViewById(R.id.day_dot) as ImageView
    }
}