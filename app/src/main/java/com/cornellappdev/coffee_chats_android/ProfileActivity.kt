package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.cornellappdev.coffee_chats_android.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        headerLayout.setPadding(0, 0, 0, headerLayout.paddingBottom / 4)
        increaseHitArea(backButton)
        backButton.setOnClickListener { onBackPressed() }
        headerText.text = intent.extras?.getString(HEADER_TEXT) ?: ""
        intent.extras?.getBoolean(ENABLE_EDIT).let {
            save_button.visibility = View.VISIBLE
            save_button.text = getString(R.string.edit)
        }

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragmentContainer,
                ProfileFragment.newInstance(intent.extras!!.getInt(USER_ID))
            )
        }
    }

    companion object {
        const val USER_ID = "USER_ID"
        const val HEADER_TEXT = "HEADER_TEXT"
        const val ENABLE_EDIT = "ENABLE_EDIT"
    }
}