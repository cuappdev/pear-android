package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.activity_onboarding.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        headerLayout.setPadding(0, 0, 0, headerLayout.paddingBottom / 4)
        onboarding_next.visibility = View.GONE
        add_later.visibility = View.GONE
        increaseHitArea(back_button)
        back_button.setOnClickListener { onBackPressed() }
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
    }
}