package com.cornellappdev.coffee_chats_android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cornellappdev.coffee_chats_android.R


class PlacesAdapter(private val mContext: Context,
                    private val places: Array<kotlin.String>
): BaseAdapter() {

    var selectedPositions = mutableListOf<Int>()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val placeView:View = inflater.inflate(R.layout.location_scheduling_places_layout, null)
        val placeTextView = placeView.findViewById<TextView>(R.id.placeTextView)
        placeTextView.text = places[position]
        return placeView
    }

    override fun getItem(position: Int): Any {
        return places[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return places.size
    }

}