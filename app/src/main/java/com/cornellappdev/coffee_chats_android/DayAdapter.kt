package com.cornellappdev.coffee_chats_android

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView


class DayAdapter(private val mContext: Context,
                    private val days: Array<String>
): BaseAdapter() {

//    var selectedPositions = mutableListOf<Int>()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dayView:View = inflater.inflate(R.layout.time_scheduling_day_layout, null)
        val dayTextView = dayView.findViewById<TextView>(R.id.day_button)
        dayTextView.text = days[position]
        val dayDot = dayView.findViewById<ImageView>(R.id.day_dot)
        if (position == 0) dayDot.visibility = View.VISIBLE
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

}