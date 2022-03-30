package com.cornellappdev.coffee_chats_android.fragments.messaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.adapters.MessageAdapter
import com.cornellappdev.coffee_chats_android.networking.getCurrentMatch
import com.cornellappdev.coffee_chats_android.networking.getSelfMatches
import kotlinx.android.synthetic.main.fragment_chat.recyclerView
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Displays list of all pears that can be messages
 */
class MessagesFragment : Fragment() {
    private var userId: Int? = null
    private lateinit var adapter: MessageAdapter
    private lateinit var container: MessagesContainer

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
        CoroutineScope(Dispatchers.Main).launch {
            val currentPearId = getCurrentMatch()?.matchedUser?.id
            if (currentPearId != null) {
                adapter = MessageAdapter(getSelfMatches(userId!!), currentPearId) {
                    container.addChatFragment(userId!!, it.id, it.firstName, it.profilePicUrl!!)
                }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            } else {
                emptyMessagesView.visibility = View.VISIBLE
            }
        }
    }

    fun setContainer(container: MessagesContainer) {
        this.container = container
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

    interface MessagesContainer {
        fun addChatFragment(
            userId: Int,
            pearId: Int,
            pearFirstName: String,
            pearProfilePicUrl: String
        )
    }
}