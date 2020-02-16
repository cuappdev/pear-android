package com.example.coffee_chats_android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class ClubInterestAdapter(private val mContext: Context, list: Array<String>) :
    ArrayAdapter<String?>(mContext, 0, list) {
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
        clubOrInterestText.setText(currentClubInterest)
//        val image =
//            listItem!!.findViewById<View>(R.id.imageView_poster) as ImageView
//        image.setImageResource(currentMovie.getmImageDrawable())
//        val name =
//            listItem.findViewById<View>(R.id.textView_name) as TextView
//        name.setText(currentMovie.getmName())
//        val release =
//            listItem.findViewById<View>(R.id.textView_release) as TextView
//        release.setText(currentMovie.getmRelease())
        return listItem
    }

    init {
        clubInterestList = list
    }
}