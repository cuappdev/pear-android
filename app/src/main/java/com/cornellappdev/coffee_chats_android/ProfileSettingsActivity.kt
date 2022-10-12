package com.cornellappdev.coffee_chats_android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cornellappdev.coffee_chats_android.fragments.*
import com.cornellappdev.coffee_chats_android.utils.PopupManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity(), OnFilledOutListener, OnPauseChangedListener {
    private val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
    private lateinit var content: Content
    private var isPaused: Boolean = false
    private var popup: PopupWindow? = null

    /** Pages directly reachable from drawer */
    private val basePages =
        listOf(Content.EDIT_INFO, Content.EDIT_INTERESTS, Content.EDIT_GROUPS, Content.SETTINGS)

    /** Fragments nested within settings */
    private val settingsSubPages = listOf(
        Content.SOCIAL_MEDIA,
        Content.ABOUT
    )

    /** Fragments where users can edit and save information */
    private val editPages = listOf(
        Content.EDIT_INFO,
        Content.EDIT_GROUPS,
        Content.SOCIAL_MEDIA,
        Content.EDIT_INTERESTS
    )

    enum class Content {
        EDIT_INFO,
        EDIT_INTERESTS,
        EDIT_GROUPS,
        SETTINGS,
        SOCIAL_MEDIA,
        ABOUT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        content = intent.getSerializableExtra(CONTENT) as Content
        isPaused = intent.getBooleanExtra(IS_PAUSED, isPaused)
        val fragment: Fragment = when (content) {
            Content.EDIT_INFO -> EditProfileFragment()
            Content.EDIT_INTERESTS -> EditInterestsGroupsFragment.newInstance(true)
            Content.EDIT_GROUPS -> EditInterestsGroupsFragment.newInstance(false)
            Content.SETTINGS -> SettingsFragment()
            Content.SOCIAL_MEDIA -> SocialMediaFragment()
            Content.ABOUT -> AboutFragment()
        }
        ft.add(fragmentContainer.id, fragment, content.name).addToBackStack("ft").commit()

        increaseHitArea(backButton)
        backButton.setOnClickListener { onBackPressed() }
        save_button.setOnClickListener { onSave(it) }
        setUpCurrentPage()
    }

    override fun onBackPressed() {
        if (popup?.isShowing == true) {
            popup!!.dismiss()
        } else if (content in basePages) {
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

    private fun onSave(view: View) {
        if (content in editPages) {
            val fragment =
                supportFragmentManager.findFragmentByTag(content.name) as OnFilledOutObservable
            fragment.saveInformation()
            // hide keyboard
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
            onBackPressed()
        }
    }

    val settingsNavigationListener = fun(menuItem: MenuItem): Boolean {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        when (menuItem.itemId) {
            R.id.nav_social_media -> {
                content = Content.SOCIAL_MEDIA
                setUpCurrentPage()
                ft.replace(fragmentContainer.id, SocialMediaFragment(), content.name)
                    .addToBackStack("ft")
                    .commit()
            }
            R.id.nav_about -> {
                content = Content.ABOUT
                setUpCurrentPage()
                ft.replace(fragmentContainer.id, AboutFragment(), content.name)
                    .addToBackStack("ft")
                    .commit()
            }
            R.id.nav_logout -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestProfile()
                    .build()
                GoogleSignIn.getClient(this, gso).signOut()
                Firebase.auth.signOut()
                val data = Intent()
                setResult(Activity.RESULT_OK, data)
                finish()
            }
            R.id.nav_pause_pear -> {
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                // dims the background when popup shows
                val backgroundView = ConstraintLayout(this)
                backgroundView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.background_dimmer
                    )
                )
                val background = PopupWindow(
                    backgroundView,
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    false,
                )
                background.showAtLocation(headerText, Gravity.CENTER, 0, 0)
                // pause pear popup
                val popupView = inflater.inflate(R.layout.pause_pear_popup, activity_main, false)
                popup = PopupWindow(
                    popupView,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    true
                )
                popup!!.showAtLocation(headerText, Gravity.CENTER, 0, 0)
                popup!!.setOnDismissListener { background.dismiss() }
                PopupManager(
                    this,
                    popup!!,
                    if (isPaused) PopupManager.PopupState.UNPAUSE else PopupManager.PopupState.PAUSE,
                    this
                )
            }
        }
        return true
    }

    private fun setUpCurrentPage() {
        headerText.text = when (content) {
            Content.EDIT_INFO -> getString(R.string.edit_info)
            Content.EDIT_INTERESTS -> getString(R.string.edit_interests)
            Content.EDIT_GROUPS -> getString(R.string.edit_groups)
            Content.SETTINGS -> getString(R.string.settings)
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

    override fun onPauseChanged(isPaused: Boolean) {
        this.isPaused = isPaused
    }

    companion object {
        const val CONTENT = "content"
        const val IS_PAUSED = "isPaused"
    }
}