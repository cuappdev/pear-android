package com.cornellappdev.coffee_chats_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.adapters.PeopleAdapter
import com.cornellappdev.coffee_chats_android.hideKeyboard
import com.cornellappdev.coffee_chats_android.networking.getAllUsers
import kotlinx.android.synthetic.main.fragment_people.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PeopleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateUsers(query)
                hideKeyboard(requireContext(), search)
                search.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean = false

        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        updateUsers()
    }

    // Retrieves list of users from backend and updates recyclerview, with an optional query
    private fun updateUsers(query: String? = "") {
        CoroutineScope(Dispatchers.Main).launch {
            val users = getAllUsers(query ?: "")
            recyclerView.adapter = PeopleAdapter(users)
        }
    }
}