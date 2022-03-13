package com.cornellappdev.coffee_chats_android.fragments.messaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.adapters.ChatAdapter
import com.cornellappdev.coffee_chats_android.models.Message
import com.cornellappdev.coffee_chats_android.utils.MessageObserver
import com.cornellappdev.coffee_chats_android.utils.getMessages
import com.cornellappdev.coffee_chats_android.utils.sendMessage
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * Displays a conversation with a single pear
 */
class ChatFragment : Fragment(), MessageObserver {
    private var userId: Int? = null
    private var pearId: Int? = null
    private var pearProfilePicUrl: String? = null
    private val messages: MutableList<Message> = mutableListOf()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID)
            pearId = it.getInt(PEAR_ID)
            pearProfilePicUrl = it.getString(PEAR_PROFILE_PIC_URL)
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
        adapter = ChatAdapter(messages, userId!!, pearProfilePicUrl!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        sendMessageButton.setOnClickListener { sendMessage() }
    }

    override fun onMessageReceived(message: Message) {
        // insert new message into message list, sorted by timestamp
        val insertionIndex =
            messages.indexOfFirst { it.time.toDouble() > message.time.toDouble() }.let {
                if (it == -1) messages.size else it
            }
        messages.add(insertionIndex, message)
        // update this message and surrounding messages to avoid datestamp issues
        adapter.notifyItemInserted(insertionIndex)
        if (insertionIndex - 1 >= 0) {
            adapter.notifyItemChanged(insertionIndex - 1)
        }
        if (insertionIndex + 1 < messages.size) {
            adapter.notifyItemChanged(insertionIndex + 1)
        }
        recyclerView.scrollToPosition(adapter.itemCount - 1)
        if (emptyChatView.visibility == View.VISIBLE) {
            emptyChatView.visibility = View.GONE
        }
    }

    override fun onMessageSendFailed() {
        Toast.makeText(requireContext(), R.string.message_send_error, Toast.LENGTH_SHORT).show()
    }

    private fun sendMessage() {
        sendMessage(sendMessageEditText.text.toString(), userId!!, pearId!!, this)
        sendMessageEditText.text.clear()
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int, pearId: Int, pearProfilePicUrl: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                    putInt(PEAR_ID, pearId)
                    putString(PEAR_PROFILE_PIC_URL, pearProfilePicUrl)
                }
            }

        private const val USER_ID = "userId"
        private const val PEAR_ID = "pearId"
        private const val PEAR_PROFILE_PIC_URL = "pearProfilePicUrl"
    }
}