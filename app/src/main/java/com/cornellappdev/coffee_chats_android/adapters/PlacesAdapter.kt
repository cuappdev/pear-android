package com.cornellappdev.coffee_chats_android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.cornellappdev.coffee_chats_android.R


class PlacesAdapter(private val mContext: Context,
                    private val places: Array<String>,
                    private val selected: MutableList<String>
): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val placeView:View
        if (convertView == null) {
            placeView = LayoutInflater.from(mContext).inflate(R.layout.location_scheduling_places_layout, parent, false)
            viewHolder = ViewHolder(placeView)
            placeView.tag = viewHolder
        } else {
            placeView = convertView
            viewHolder = placeView.tag as ViewHolder
        }

        viewHolder.placeTextView!!.text = places[position]
        if (selected.contains(places[position])) {
            viewHolder.placeSchedulingView!!.background = AppCompatResources.getDrawable(
                mContext,
                R.drawable.location_scheduling_places_selected
            )
        } else {
            viewHolder.placeSchedulingView!!.background = AppCompatResources.getDrawable(
                mContext,
                R.drawable.location_scheduling_places_unselected
            )
        }
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

    private class ViewHolder(view: View?) {
        val placeTextView = view?.findViewById<TextView>(R.id.placeTextView)
        val placeSchedulingView = view?.findViewById<ConstraintLayout>(R.id.place_scheduling_view)
    }

}