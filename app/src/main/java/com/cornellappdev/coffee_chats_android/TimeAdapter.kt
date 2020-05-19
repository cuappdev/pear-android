package com.cornellappdev.coffee_chats_android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class TimeAdapter(private val mContext: Context,
                    private val times: Array<kotlin.String>
): BaseAdapter() {

    var selectedPositions = mutableListOf<Int>()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val timeView: View

        if (convertView == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            timeView = inflater.inflate(R.layout.time_option_item, null)
            viewHolder = ViewHolder(timeView)
            timeView.tag = viewHolder
        } else {
            timeView = convertView
            viewHolder = timeView.tag as ViewHolder
        }
        viewHolder.timeButton.text = times[position]
        return timeView
    }

    override fun getItem(position: Int): Any {
        return times[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return times.size
    }

    private class ViewHolder(view: View?) {
        val timeButton = view?.findViewById(R.id.time_option_text) as TextView
    }
}