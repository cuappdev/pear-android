package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cornellappdev.coffee_chats_android.fragments.*
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileActivity : AppCompatActivity(), OnFilledOutListener {

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
            save_button.setOnClickListener {
                tabLayout.visibility = View.VISIBLE
                viewPager.adapter = ViewPagerAdapter(this)
                TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
                fragmentContainer.visibility = View.GONE
            }
        }

        fragmentContainer.visibility = View.VISIBLE
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragmentContainer,
                ProfileFragment.newInstance(intent.extras!!.getInt(USER_ID))
            )
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnFilledOutObservable) {
            fragment.setOnFilledOutListener(this)
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
                0 -> EditProfileFragment()
                1 -> EditInterestsGroupsFragment.newInstance(true)
                2 -> EditInterestsGroupsFragment.newInstance(false)
                3 -> EditProfileFragment()
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