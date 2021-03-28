package com.cornellappdev.coffee_chats_android

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.TouchDelegate
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_scheduling.*

class ProfileSettingsActivity : AppCompatActivity(), SchedulingTimeFragment.OnFilledOutListener {
    private val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
    private var content = Content.SETTINGS

    enum class Content {
        SETTINGS,
        EDIT_TIME
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduling)

        content = intent.getSerializableExtra("content") as Content
        val fragment: Fragment = when (content) {
            Content.SETTINGS -> SettingsFragment()
            Content.EDIT_TIME -> SchedulingTimeFragment()
        }
        ft.add(body_fragment.id, fragment, content.name).addToBackStack("ft").commit()

        scheduling_finish.visibility = View.GONE
        increaseHitArea(nav_button)
        nav_button.setOnClickListener { onBackPressed() }
        save_button.setOnClickListener { onSave() }
        setUpCurrentPage()
    }

    override fun onBackPressed() {
        val baseFragments = listOf(Content.SETTINGS)
        val settingsSubPages = listOf(Content.EDIT_TIME)
        if (content in baseFragments) {
            finish()
        } else if (content in settingsSubPages) {
            supportFragmentManager.popBackStack()
            content = Content.SETTINGS
            setUpCurrentPage()
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is SchedulingTimeFragment) {
            fragment.setOnFilledOutListener(this)
        }
    }

    private fun onSave() {
        when (content) {
            Content.EDIT_TIME -> {
                val timeFragment =
                    supportFragmentManager.findFragmentByTag(Content.EDIT_TIME.name) as SchedulingTimeFragment
                timeFragment.updateSchedule()
                onBackPressed()
            }
        }
    }

    val settingsNavigationListener = fun(menuItem: MenuItem): Boolean {
        val itemPressed = when (menuItem.itemId) {
            R.id.nav_availabilities -> "Edit Availabilities"
            R.id.nav_social_media -> "Connect Social Media"
            R.id.nav_about -> "About Pear"
            R.id.nav_logout -> "Log Out"
            else -> "Impossible"
        }
        Log.d("NAV_ITEM_LISTENER", itemPressed)

        when (menuItem.itemId) {
            R.id.nav_availabilities -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                content = Content.EDIT_TIME
                setUpCurrentPage()
                ft.replace(body_fragment.id, SchedulingTimeFragment(), content.name)
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
            Content.SETTINGS -> getString(R.string.settings)
            Content.EDIT_TIME -> getString(R.string.edit_availability)
        }
        save_button.visibility = if (content != Content.EDIT_TIME) View.GONE else View.VISIBLE
    }

    /**
     * Increases hit area of `view` on all four sides by given `padding`, which defaults to 100
     */
    private fun increaseHitArea(view: View, padding: Int = 100) {
        val parent = view.parent as View
        parent.post {
            val rect = Rect()
            nav_button.getHitRect(rect)
            rect.top -= padding
            rect.left -= padding
            rect.bottom += padding
            rect.right += padding
            parent.touchDelegate = TouchDelegate(rect, nav_button)
        }
    }

    override fun onFilledOut() {
        save_button.isEnabled = true
    }

    override fun onSelectionEmpty() {
        save_button.isEnabled = false
    }
}