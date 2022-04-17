package com.cornellappdev.coffee_chats_android

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.fragments.NoMatchFragment
import com.cornellappdev.coffee_chats_android.fragments.PeopleFragment
import com.cornellappdev.coffee_chats_android.fragments.ProfileFragment
import com.cornellappdev.coffee_chats_android.models.User
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.cornellappdev.coffee_chats_android.networking.setUpNetworking
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_scheduling.*
import kotlinx.android.synthetic.main.nav_header_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SchedulingActivity : AppCompatActivity() {
    private lateinit var user: User
    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduling)

        // determine if the app should show scheduling page, sign-in, or onboarding

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null && preferencesHelper.accessToken != null && preferencesHelper.accessToken!!.isNotEmpty()) {
            // user is already signed in - get user profile
            CoroutineScope(Dispatchers.Main).launch {
                setUpNetworking(preferencesHelper.accessToken!!)
                try {
                    user = getUser()
                    // move to onboarding if user hasn't finished
                    if (!user.hasOnboarded) {
                        val intent = Intent(applicationContext, OnboardingActivity::class.java)
                        intent.putExtra(ACCESS_TOKEN_TAG, preferencesHelper.accessToken!!)
                        startActivity(intent)
                    } else {
                        setUpDrawerLayout()
                        val c = this@SchedulingActivity
                        val isUserMatched = user.currentMatch != null
                        viewPager.adapter =
                            ViewPagerAdapter(c, isUserMatched)
                        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                            tab.text =
                                if (position == 0) c.getText(R.string.match_header)
                                else c.getText(R.string.people_header)
                        }.attach()
                        // resize tabs so they wrap tab text
                        tabLayout.apply {
                            for (i in 0 until NUM_FRAGMENTS) {
                                val layout =
                                    (this.getChildAt(0) as LinearLayout).getChildAt(0) as LinearLayout
                                val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
                                layoutParams.weight = 0f
                                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                                layout.layoutParams = layoutParams
                            }
                        }
                    }
                } catch (e: Exception) {
                    // login error, prompt user to sign in
                    signIn()
                }
            }
        } else {
            // prompt user to log in
            signIn()
        }
        // hide fragment container and header- replaced by TabLayout + ViewPager
        fragmentContainer.visibility = View.GONE
        headerText.visibility = View.GONE
        tabLayout.visibility = View.VISIBLE
        feedbackButton.setOnClickListener {
            showPopup(it)
        }

        // set up navigation view
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.itemIconTintList = null
        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.close()
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            val contentTag = when (menuItem.itemId) {
                R.id.nav_interests -> ProfileSettingsActivity.Content.EDIT_INTERESTS
                R.id.nav_groups -> ProfileSettingsActivity.Content.EDIT_GROUPS
                R.id.nav_settings -> ProfileSettingsActivity.Content.SETTINGS
                else -> null
            }
            contentTag?.let { intent.putExtra(ProfileSettingsActivity.CONTENT, contentTag) }
            when (menuItem.itemId) {
                R.id.nav_settings -> startActivityForResult(intent, SETTINGS_CODE)
                R.id.nav_interests, R.id.nav_groups -> startActivity(intent)
                R.id.nav_messages -> {
                    val messagingIntent = Intent(this, MessagingActivity::class.java).apply {
                        putExtra(MessagingActivity.STAGE, MessagingActivity.Stage.MESSAGES)
                        putExtra(MessagingActivity.USER_ID, user.id)
                    }
                    startActivity(messagingIntent)
                }
            }
            true
        }
        setUpCurrentPage()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        CoroutineScope(Dispatchers.Main).launch {
            user = getUser()
            setUpDrawerLayout()
        }
    }

    /**
     * Populates drawer layout with user information and adds a listener so the contents of the
     * page move when the drawer slides.
     */
    private fun setUpDrawerLayout() {
        Glide.with(applicationContext).load(user.profilePicUrl).centerInside().circleCrop()
            .into(drawerLayout.user_image)
        Glide.with(applicationContext).load(user.profilePicUrl).centerInside().circleCrop()
            .into(backButton)
        drawerLayout.user_name.text =
            getString(R.string.user_name, user.firstName, user.lastName)
        drawerLayout.user_major_year.text = getString(
            R.string.user_major_year,
            if (user.majors.isNotEmpty()) user.majors.first().name else "",
            user.graduationYear
        )
        drawerLayout.user_hometown.text = getString(R.string.user_hometown, user.hometown)
        val content = findViewById<ConstraintLayout>(R.id.activity_main)
        drawerLayout.edit_info.setOnClickListener {
            Intent(this, ProfileSettingsActivity::class.java).apply {
                putExtra(ProfileSettingsActivity.CONTENT, ProfileSettingsActivity.Content.EDIT_INFO)
                startActivity(this)
            }
        }
        drawerLayout.addDrawerListener(object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                content.translationX = drawerView.width * slideOffset
            }
        })
    }

    /**
     * Navigates to `SignInActivity`
     */
    private fun signIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isOpen) {
            drawerLayout.close()
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (drawerLayout.isOpen) {
            drawerLayout.close()
        }
        if (::user.isInitialized) {
            CoroutineScope(Dispatchers.Main).launch {
                user = getUser()
                setUpDrawerLayout()
                setUpCurrentPage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // user has logged out from Settings
        if (requestCode == SETTINGS_CODE && resultCode == Activity.RESULT_OK) {
            preferencesHelper.clearLogin()
            signIn()
        }
    }

    private fun setUpCurrentPage() {
        val displayMetrics = Resources.getSystem().displayMetrics
        // clear back caret icon
        backButton.background = null
        backButton.layoutParams = backButton.layoutParams.apply {
            height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, displayMetrics)
                .toInt()
            width = height
        }
        increaseHitArea(backButton)
        backButton.setOnClickListener {
            if (drawerLayout.isOpen) {
                drawerLayout.close()
            } else {
                drawerLayout.open()
            }
        }
        feedbackButton.visibility = View.VISIBLE
    }

    private fun showPopup(v: View) {
        // style wrapper
        val wrapper = ContextThemeWrapper(this, R.style.popUpTheme_PopupMenu)
        val popup = PopupMenu(wrapper, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.feedback_menu, popup.menu)
        popup.show()

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.nav_send_feedback -> {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(getString(R.string.feedback_url))
                    startActivity(i)
                    true
                }
                R.id.nav_contact_us -> {
                    sendEmail(
                        getString(R.string.feedback_email),
                        getString(R.string.feedback_contact)
                    )
                    true
                }
                R.id.nav_report_user -> {
                    sendEmail(
                        getString(R.string.feedback_email),
                        getString(R.string.feedback_report)
                    )
                    true
                }
            }
            false
        }
    }

    private fun sendEmail(recipient: String, subject: String) {
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"

        //puts in recipient of email
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //puts in the subject for the email
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //opens the email chooser
        startActivity(Intent.createChooser(mIntent, "Choose Email Application..."))
    }

    // adapter for tabs
    private inner class ViewPagerAdapter(activity: SchedulingActivity, val isUserMatched: Boolean) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return NUM_FRAGMENTS
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    if (isUserMatched)
                        ProfileFragment.newInstance(user.currentMatch!!.matchedUser, user.id)
                    else NoMatchFragment()
                }
                else -> PeopleFragment()
            }
        }
    }

    companion object {
        private const val NUM_FRAGMENTS = 2
        private const val SETTINGS_CODE = 10032
    }
}