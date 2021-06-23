package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_match_profile.*
import kotlinx.android.synthetic.main.pill_view.view.*


class MatchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_match_profile, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val interests = listOf("Reading", "Coding", "Crosswords", "Jigsaw", "Jenga")
        (interests.indices).forEach { i ->
            val pillView = LayoutInflater.from(requireContext()).inflate(
                R.layout.pill_view,
                interests_pill_list,
                false
            ).apply {
                id = View.generateViewId()
            }
            pillView.basic_info.text = interests[i]
            interests_pill_list.addView(pillView)
            interests_pill_flow.addView(pillView)
        }
        val groups = listOf("Flat Earth Society", "Cornell AppDev")
        (groups.indices).forEach { i ->
            val pillView = LayoutInflater.from(requireContext()).inflate(
                R.layout.pill_view,
                groups_pill_list,
                false
            ).apply {
                id = View.generateViewId()
            }
            pillView.basic_info.text = groups[i]
            groups_pill_list.addView(pillView)
            groups_pill_flow.addView(pillView)
        }

    }
}