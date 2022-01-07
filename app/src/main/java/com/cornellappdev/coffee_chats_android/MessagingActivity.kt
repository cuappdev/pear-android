package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.cornellappdev.coffee_chats_android.fragments.messaging.ChatFragment
import com.cornellappdev.coffee_chats_android.fragments.messaging.MessagesFragment
import kotlinx.android.synthetic.main.activity_messaging.*

class MessagingActivity : AppCompatActivity(), MessagesFragment.MessagesContainer {

    enum class Stage {
        // list of all past matches
        MESSAGES,
        // chat with single match
        CHAT
    }

    private lateinit var initialStage: Stage
    private var stage = Stage.MESSAGES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        if (savedInstanceState == null) {
            initialStage = intent.extras!!.getSerializable(STAGE) as Stage
            stage = initialStage
            increaseHitArea(backButton)
            backButton.setOnClickListener { onBackPressed() }
            when (stage) {
                Stage.CHAT -> {
                    intent.extras?.let {
                        addChatFragment(
                            it.getInt(USER_ID),
                            it.getInt(PEAR_ID),
                            it.getString(PEAR_FIRST_NAME)!!,
                            it.getString(PEAR_PROFILE_PIC_URL)!!
                        )
                    }
                }
                Stage.MESSAGES -> {
                    intent.extras?.let {
                        setUpHeader()
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(
                                R.id.fragmentContainer,
                                MessagesFragment.newInstance(it.getInt(USER_ID))
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets up header and subheader, depending on `stage`
     */
    private fun setUpHeader(firstName: String = "") {
        when (stage) {
            Stage.CHAT -> {
                headerText.text = getString(R.string.chat_header, firstName)
                headerSubtext.visibility = View.GONE
            }
            Stage.MESSAGES -> {
                headerText.text = getString(R.string.messages_header)
                headerSubtext.text = getString(R.string.messages_subheader)
                headerSubtext.visibility = View.VISIBLE
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is MessagesFragment) {
            fragment.setContainer(this)
        }
    }

    override fun onBackPressed() {
        hideKeyboard(this, backButton)
        if (stage == Stage.CHAT && initialStage == Stage.MESSAGES) {
            stage = Stage.MESSAGES
            setUpHeader()
        }
        // needed because chat fragment is added to back stack
        if (initialStage == Stage.CHAT) {
            supportFragmentManager.popBackStack()
        }
        super.onBackPressed()
    }

    companion object {
        const val STAGE = "stage"
        const val USER_ID = "userId"
        const val PEAR_ID = "pearId"
        const val PEAR_FIRST_NAME = "pearFirstName"
        const val PEAR_PROFILE_PIC_URL = "pearProfilePicUrl"
    }

    override fun addChatFragment(userId: Int, pearId: Int, pearFirstName: String, pearProfilePicUrl: String) {
        stage = Stage.CHAT
        setUpHeader(pearFirstName)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragmentContainer,
                ChatFragment.newInstance(userId, pearId, pearProfilePicUrl)
            )
            addToBackStack(Stage.CHAT.toString())
        }
    }
}