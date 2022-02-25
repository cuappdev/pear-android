package com.cornellappdev.coffee_chats_android

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.fragments.NoMatchFragment
import com.cornellappdev.coffee_chats_android.fragments.ProfileFragment
import com.cornellappdev.coffee_chats_android.fragments.SchedulingPlaceFragment
import com.cornellappdev.coffee_chats_android.fragments.SchedulingTimeFragment
import com.cornellappdev.coffee_chats_android.models.User
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.cornellappdev.coffee_chats_android.networking.setUpNetworking
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_scheduling.*
import kotlinx.android.synthetic.main.nav_header_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SchedulingActivity :
    AppCompatActivity(),
    OnFilledOutListener {
    private lateinit var user: User
    private var page = 0        // 0: no match; 1: time scheduling; 2: place scheduling
    private val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }
    private val noMatchTag = "NO_MATCH"
    private val matchTag = "MATCH"
    private val scheduleTimeTag = "SCHEDULING_TIME"
    private val schedulePlaceTag = "SCHEDULING_PLACE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduling)

        // determine if the app should show scheduling page, sign-in, or onboarding

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null && preferencesHelper.accessToken != null && preferencesHelper.accessToken!!.isNotEmpty()) {
            // user is already signed in - get user profile
            CoroutineScope(Dispatchers.Main).launch {
                setUpNetworking(preferencesHelper.accessToken!!)
                user = getUser()
                // move to onboarding if user hasn't finished
                if (!user.hasOnboarded) {
                    val intent = Intent(applicationContext, OnboardingActivity::class.java)
                    intent.putExtra(ACCESS_TOKEN_TAG, preferencesHelper.accessToken!!)
                    startActivity(intent)
                } else {
                    setUpDrawerLayout()
                    if (user.currentMatch == null) {
                        primaryActionButton.visibility = View.GONE
                        ft.add(fragmentContainer.id, NoMatchFragment()).addToBackStack(noMatchTag)
                        ft.commit()
                        headerText.text = getString(R.string.no_match_header)
                    } else {
                        ft.add(
                            fragmentContainer.id,
                            ProfileFragment.newInstance(user.currentMatch!!.matchedUser)
                        ).addToBackStack(matchTag)
                        ft.commit()
                        headerText.text = getString(R.string.match_header)
                    }
                }
            }
        } else {
            // prompt user to log in
            signIn()
        }

        primaryActionButton.setOnClickListener {
            onSendMessageClick()
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
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnFilledOutObservable) {
            fragment.setOnFilledOutListener(this)
        }
    }

    override fun onFilledOut() {
        primaryActionButton.isEnabled = true
    }

    override fun onSelectionEmpty() {
        primaryActionButton.isEnabled = false
    }

    override fun onBackPressed() {
        if (drawerLayout.isOpen) {
            drawerLayout.close()
        } else if (page > 0) {
            onBackPage()
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

    private fun onBackPage() {
        page--
        supportFragmentManager.popBackStack()
        setUpCurrentPage()
    }

    private fun onSendMessageClick() {
        user.currentMatch?.let {
            val match = it.matchedUser
            val messagingIntent = Intent(this, MessagingActivity::class.java).apply {
                putExtra(MessagingActivity.STAGE, MessagingActivity.Stage.CHAT)
                putExtra(MessagingActivity.USER_ID, user.id)
                putExtra(MessagingActivity.PEAR_ID, match.id)
                putExtra(MessagingActivity.PEAR_FIRST_NAME, match.firstName)
                putExtra(MessagingActivity.PEAR_PROFILE_PIC_URL, match.profilePicUrl)
            }
            startActivity(messagingIntent)
        }
    }

    private fun onNextPage() {
        if (page == 2) {
            val locationFragment =
                supportFragmentManager.findFragmentByTag(schedulePlaceTag) as SchedulingPlaceFragment
            locationFragment.saveInformation()
            page = 0
            setUpCurrentPage()
            supportFragmentManager.popBackStack(noMatchTag, 0)
            return
        }
        if (page < 2) page++
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (page == 1) {
            ft.replace(fragmentContainer.id, SchedulingTimeFragment(), scheduleTimeTag)
        } else {
            val timeFragment =
                supportFragmentManager.findFragmentByTag(scheduleTimeTag) as SchedulingTimeFragment
            timeFragment.saveInformation()
            ft.replace(fragmentContainer.id, SchedulingPlaceFragment(), schedulePlaceTag)
        }
        setUpCurrentPage()
        ft.addToBackStack("ft")
        ft.commit()
    }

    private fun setUpCurrentPage() {
        val displayMetrics = Resources.getSystem().displayMetrics
        if (page == 0) {
            backButton.background = ContextCompat.getDrawable(this, R.drawable.ic_sign_in_logo)
            backButton.layoutParams = backButton.layoutParams.apply {
                height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, displayMetrics)
                    .toInt()
                width = height
            }
            backButton.setOnClickListener {
                if (drawerLayout.isOpen) {
                    drawerLayout.close()
                } else {
                    drawerLayout.open()
                }
            }
            primaryActionButton.text = getString(R.string.no_match_availability)
            primaryActionButton.isEnabled = true
            primaryActionButton.setPadding(100, 0, 100, 0)
        } else {
            backButton.background = ContextCompat.getDrawable(this, R.drawable.ic_back_carrot)
            backButton.layoutParams = backButton.layoutParams.apply {
                height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18f, displayMetrics)
                    .toInt()
                width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, displayMetrics)
                    .toInt()
            }
            increaseHitArea(backButton)
            backButton.setOnClickListener {
                onBackPage()
            }
            primaryActionButton.isEnabled = false
            primaryActionButton.setPadding(180, 0, 180, 0)
            if (page == 1) {
                headerText.text = getString(R.string.scheduling_time_header)
                primaryActionButton.text = getString(R.string.scheduling_time_button)
            } else {
                headerText.text = getString(R.string.scheduling_place_header)
                primaryActionButton.text = getString(R.string.scheduling_place_button)
            }
        }
        primaryActionButton.text = getString(R.string.send_message)
    }

    companion object {
        private const val SETTINGS_CODE = 10032
    }
}