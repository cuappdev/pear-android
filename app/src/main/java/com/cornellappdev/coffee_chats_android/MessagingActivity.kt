package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.cornellappdev.coffee_chats_android.fragments.messaging.ChatFragment

class MessagingActivity : AppCompatActivity() {

    enum class STAGE {
        // list of all past matches
        MESSAGES,
        // chat with single match
        CHAT
    }

    private var stage = STAGE.MESSAGES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragmentContainer, ChatFragment.newInstance(intent.extras!!.getInt(USER_ID), DUMMY_PEAR_ID, DUMMY_PEAR_PROFILE_PIC_URL))
            }
        }
    }

    override fun onBackPressed() {
        when (stage) {
            STAGE.MESSAGES -> {
                super.onBackPressed()
            }
            STAGE.CHAT -> {
                // TODO - change fragment
            }
        }
    }

    companion object {
        const val USER_ID = "userId"
        private const val DUMMY_PEAR_ID = -1
        private const val DUMMY_PEAR_PROFILE_PIC_URL = ""
    }
}