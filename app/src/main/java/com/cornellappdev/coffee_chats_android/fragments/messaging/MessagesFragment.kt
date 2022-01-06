package com.cornellappdev.coffee_chats_android.fragments.messaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.adapters.MessageAdapter
import com.cornellappdev.coffee_chats_android.models.Major
import com.cornellappdev.coffee_chats_android.models.MatchedUser
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * Displays list of all pears that can be messages
 */
class MessagesFragment : Fragment() {
    private var userId: Int? = null
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO populate adapter with networking info
        adapter = MessageAdapter(listOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        /**
         * @param userId Id of current user
         * @return A new instance of fragment MessagesFragment.
         */
        @JvmStatic
        fun newInstance(userId: Int) =
            MessagesFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                }
            }

        private const val USER_ID = "userId"
    }
}