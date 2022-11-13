package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cornellappdev.coffee_chats_android.fragments.*
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.cornellappdev.coffee_chats_android.singletons.UserSingleton
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_profile_settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity(), OnFilledOutListener {

    enum class State {
        PREVIEW,
        EDIT
    }

    var state = State.PREVIEW
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        headerLayout.setPadding(0, 0, 0, headerLayout.paddingBottom / 4)
        increaseHitArea(backButton)
        backButton.setOnClickListener { onBackPressed() }
        headerText.text = intent.extras?.getString(HEADER_TEXT) ?: ""
        intent.extras?.getBoolean(ENABLE_EDIT).let {
            save_button.visibility = View.VISIBLE
            save_button.setOnClickListener {
                when (state) {
                    State.PREVIEW -> {
                        state = State.EDIT
                        setUpPage()
                    }
                    State.EDIT -> {
                        UserSingleton.saveUserInfo()
                    }
                }
            }
        }

        userId = intent.extras!!.getInt(USER_ID)
        setUpPage()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnFilledOutObservable) {
            fragment.setOnFilledOutListener(this)
        }
    }

    override fun onBackPressed() {
        if (state == State.EDIT) {
            state = State.PREVIEW
            setUpPage()
        } else {
            super.onBackPressed()
        }
        hideKeyboard(this, backButton)
    }

    /** Sets up page UI based on current state */
    private fun setUpPage() {
        when (state) {
            State.PREVIEW -> {
                save_button.text = getString(R.string.edit)
                tabLayout.visibility = View.GONE
                fragmentContainer.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    val user = getUser()
                    UserSingleton.initializeUser(user)
                }
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(
                        R.id.fragmentContainer,
                        ProfileFragment.newInstance(userId)
                    )
                }
            }
            State.EDIT -> {
                save_button.text = getString(R.string.save)
                tabLayout.visibility = View.VISIBLE
                viewPager.adapter = ViewPagerAdapter(this)
                TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
                fragmentContainer.visibility = View.GONE
            }
        }
    }

    // adapter for tabs
    private inner class ViewPagerAdapter(activity: ProfileActivity) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return NUM_FRAGMENTS
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> EditProfileFragment.newInstance(isOnboarding = false, useSingleton = true)
                1 -> EditInterestsGroupsFragment.newInstance(isInterest = true)
                2 -> EditInterestsGroupsFragment.newInstance(isInterest = false)
                3 -> EditProfileFragment() // TODO replace with PromptsFragment
                else -> throw IllegalStateException()
            }
        }
    }

    companion object {
        const val USER_ID = "USER_ID"
        const val HEADER_TEXT = "HEADER_TEXT"
        const val ENABLE_EDIT = "ENABLE_EDIT"
        const val NUM_FRAGMENTS = 4
    }

    override fun onFilledOut() {
        // TODO: Implement
    }

    override fun onSelectionEmpty() {
        // TODO: Implement
    }
}