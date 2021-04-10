package com.cornellappdev.coffee_chats_android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_scheduling.*

class ProfileSettingsActivity : AppCompatActivity(), OnFilledOutListener {
    private val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
    private lateinit var content: Content

    /** Pages directly reachable from drawer */
    private val basePages = listOf(Content.EDIT_INTERESTS, Content.EDIT_GROUPS, Content.SETTINGS)

    /** Fragments nested within settings */
    private val settingsSubPages = listOf(
        Content.EDIT_TIME,
        Content.EDIT_LOCATION,
        Content.SOCIAL_MEDIA,
        Content.ABOUT
    )

    /** Fragments where users can edit and save information */
    private val editPages = listOf(
        Content.EDIT_TIME,
        Content.EDIT_GROUPS,
        Content.EDIT_LOCATION,
        Content.SOCIAL_MEDIA,
        Content.EDIT_INTERESTS
    )

    enum class Content {
        EDIT_INTERESTS,
        EDIT_GROUPS,
        SETTINGS,
        EDIT_TIME,
        EDIT_LOCATION,
        SOCIAL_MEDIA,
        ABOUT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduling)
        content = intent.getSerializableExtra("content") as Content
        val fragment: Fragment = when (content) {
            Content.EDIT_INTERESTS -> EditInterestsGroupsFragment.newInstance(true)
            Content.EDIT_GROUPS -> EditInterestsGroupsFragment.newInstance(false)
            Content.SETTINGS -> SettingsFragment()
            Content.EDIT_TIME -> SchedulingTimeFragment()
            Content.EDIT_LOCATION -> SchedulingPlaceFragment()
            Content.SOCIAL_MEDIA -> SocialMediaFragment()
            Content.ABOUT -> AboutFragment()
        }
        ft.add(body_fragment.id, fragment, content.name).addToBackStack("ft").commit()

        scheduling_finish.visibility = View.GONE
        increaseHitArea(nav_button)
        nav_button.setOnClickListener { onBackPressed() }
        save_button.setOnClickListener { onSave() }
        setUpCurrentPage()
    }

    override fun onBackPressed() {
        // hide keyboard
        val inputMethodManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        if (content in basePages) {
            finish()
        } else if (content in settingsSubPages) {
            supportFragmentManager.popBackStack()
            content = Content.SETTINGS
            setUpCurrentPage()
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnFilledOutObservable) {
            fragment.setOnFilledOutListener(this)
        }
    }

    private fun onSave() {
        if (content in editPages) {
            val fragment =
                supportFragmentManager.findFragmentByTag(content.name) as OnFilledOutObservable
            fragment.saveInformation()
            onBackPressed()
        }
    }

    val settingsNavigationListener = fun(menuItem: MenuItem): Boolean {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        when (menuItem.itemId) {
            R.id.nav_availabilities -> {
                content = Content.EDIT_TIME
                setUpCurrentPage()
                ft.replace(body_fragment.id, SchedulingTimeFragment(), content.name)
                    .addToBackStack("ft")
                    .commit()
            }
            R.id.nav_location -> {
                content = Content.EDIT_LOCATION
                setUpCurrentPage()
                ft.replace(body_fragment.id, SchedulingPlaceFragment(), content.name)
                    .addToBackStack("ft")
                    .commit()
            }
            R.id.nav_social_media -> {
                content = Content.SOCIAL_MEDIA
                setUpCurrentPage()
                ft.replace(body_fragment.id, SocialMediaFragment(), content.name)
                    .addToBackStack("ft")
                    .commit()
            }
            R.id.nav_about -> {
                content = Content.ABOUT
                setUpCurrentPage()
                ft.replace(body_fragment.id, AboutFragment(), content.name)
                    .addToBackStack("ft")
                    .commit()
            }
            R.id.nav_logout -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestProfile()
                    .build()
                GoogleSignIn.getClient(this, gso).signOut()
                val data = Intent()
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
        return true
    }

    private fun setUpCurrentPage() {
        scheduling_header.text = when (content) {
            Content.EDIT_INTERESTS -> getString(R.string.edit_interests)
            Content.EDIT_GROUPS -> getString(R.string.edit_groups)
            Content.SETTINGS -> getString(R.string.settings)
            Content.EDIT_TIME, Content.EDIT_LOCATION -> getString(R.string.edit_availability)
            Content.SOCIAL_MEDIA -> getString(R.string.social_media)
            Content.ABOUT -> getString(R.string.about_pear)
        }
        save_button.visibility = if (content in editPages) View.VISIBLE else View.GONE
    }

    override fun onFilledOut() {
        save_button.isEnabled = true
    }

    override fun onSelectionEmpty() {
        save_button.isEnabled = false
    }
}