package com.cornellappdev.coffee_chats_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.adapters.PeopleAdapter
import kotlinx.android.synthetic.main.fragment_people.*

class PeopleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = PeopleAdapter(listOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}