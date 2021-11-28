package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.cornellappdev.coffee_chats_android.fragments.messaging.ChatFragment
import kotlinx.android.synthetic.main.activity_messaging.*

class MessagingActivity : AppCompatActivity() {

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
            backButton.setOnClickListener { onBackPressed() }
            when (stage) {
                Stage.CHAT -> {
                    intent.extras?.let {
                        // set up header text
                        headerText.text = getString(
                            R.string.chat_header, it.getString(
                                PEAR_FIRST_NAME
                            )
                        )
                        headerSubtext.visibility = View.GONE
                        // set up chat fragment
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.fragmentContainer,
                                ChatFragment.newInstance(
                                    it.getInt(USER_ID),
                                    it.getInt(PEAR_ID),
                                    it.getString(PEAR_PROFILE_PIC_URL)!!
                                )
                            )
                        }
                    }
                }
                Stage.MESSAGES -> TODO("Unimplemented")
            }
        }
    }

    override fun onBackPressed() {
        when (initialStage) {
            Stage.MESSAGES -> {
                TODO("Unimplemented")
            }
            Stage.CHAT -> {
                super.onBackPressed()
            }
        }
    }

    companion object {
        const val STAGE = "stage"
        const val USER_ID = "userId"
        const val PEAR_ID = "pearId"
        const val PEAR_FIRST_NAME = "pearFirstName"
        const val PEAR_PROFILE_PIC_URL = "pearProfilePicUrl"
    }
}