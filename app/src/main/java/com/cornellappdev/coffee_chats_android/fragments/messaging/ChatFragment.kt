package com.cornellappdev.coffee_chats_android.fragments.messaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.adapters.ChatAdapter
import com.cornellappdev.coffee_chats_android.models.Message
import com.cornellappdev.coffee_chats_android.utils.MessageObserver
import com.cornellappdev.coffee_chats_android.utils.addMessagesListener
import com.cornellappdev.coffee_chats_android.utils.getMessages
import com.cornellappdev.coffee_chats_android.utils.sendMessage
import kotlinx.android.synthetic.main.fragment_chat.*

private const val USER_ID = "userId"
private const val PEAR_ID = "pearId"

class ChatFragment : Fragment(), MessageObserver {
    private var userId: Int? = null
    private var pearId: Int? = null
    private val messages: MutableList<Message> = mutableListOf()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID)
            pearId = it.getInt(PEAR_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getMessages(userId!!, pearId!!, this)
        adapter = ChatAdapter(messages, userId!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onMessageReceived(message: Message) {
        // insert new message into message list, sorted by timestamp
        val insertionIndex =
            messages.indexOfFirst { it.time.toDouble() > message.time.toDouble() }.let {
                if (it == -1) messages.size else it
            }
        messages.add(insertionIndex, message)
        adapter.notifyItemInserted(insertionIndex)
    }

    override fun onMessageSendFailed() {
        // TODO - display Toast to inform users
    }

    private fun sendMessage() {
        // TODO - add EditText UI
        sendMessage("Hello yet again", userId!!, pearId!!, this)
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int, pearId: Int) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                    putInt(PEAR_ID, pearId)
                }
            }
    }
}