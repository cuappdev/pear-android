package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cornellappdev.coffee_chats_android.models.*
import com.cornellappdev.coffee_chats_android.networking.Endpoint
import com.cornellappdev.coffee_chats_android.networking.Request
import com.cornellappdev.coffee_chats_android.networking.refreshSession
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_scheduling.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class SchedulingActivity :
    AppCompatActivity(),
    SchedulingTimeFragment.OnFilledOutListener,
    SchedulingPlaceFragment.OnFilledOutListener {
    private lateinit var nextButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var profile: UserProfile
    private var page = 0        // 0: no match; 1: time scheduling; 2: place scheduling
    private val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }
    private val noMatchTag = "NO_MATCH"
    private val scheduleTimeTag = "SCHEDULING_TIME"
    private val schedulePlaceTag = "SCHEDULING_PLACE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduling)

        // Determine if the app should show scheduling page or sign-in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val isAccessTokenExpired = Date() >= Date(preferencesHelper.expiresAt * 1000)
            // refresh session if necessary
            if (isAccessTokenExpired) {
                CoroutineScope(Dispatchers.Main).launch {
                    val refreshSessionEndpoint =
                        Endpoint.refreshSession(preferencesHelper.refreshToken!!)
                    val typeToken = object : TypeToken<ApiResponse<UserSession>>() {}.type
                    val response = withContext(Dispatchers.IO) {
                        Request.makeRequest<ApiResponse<UserSession>>(
                            refreshSessionEndpoint.okHttpRequest(),
                            typeToken
                        )
                    }!!
                    if (!response.success) {
                        signIn()
                        return@launch
                    }
                    val userSession = response.data
                    preferencesHelper.accessToken = userSession.accessToken
                    preferencesHelper.refreshToken = userSession.refreshToken
                    preferencesHelper.expiresAt = userSession.sessionExpiration.toLong()
                }
            }
            UserSession.currentSession = UserSession(
                preferencesHelper.accessToken!!,
                preferencesHelper.refreshToken!!,
                preferencesHelper.expiresAt.toString(),
                true
            )
        } else {
            // prompt user to log in
            signIn()
        }

        // add fragment to body_fragment
        ft.add(body_fragment.id, NoMatchFragment()).addToBackStack(noMatchTag)
        ft.commit()

        nav_button.setOnClickListener { onBackPage() }

        scheduling_finish.setOnClickListener { onNextPage() }

        nextButton = findViewById(R.id.scheduling_finish)
        backButton = findViewById(R.id.nav_button)

        setUpCurrentPage()
    }

    private fun signIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is SchedulingTimeFragment) {
            fragment.setOnFilledOutListener(this)
        } else if (fragment is SchedulingPlaceFragment) {
            fragment.setOnFilledOutListener(this)
        }
    }

    override fun onFilledOut() {
        nextButton.isEnabled = true
    }

    override fun onSelectionEmpty() {
        nextButton.isEnabled = false
    }

    private fun onBackPage() {
        page--
        supportFragmentManager.popBackStack()
        setUpCurrentPage()
    }

    private fun onNextPage() {
        if (page == 2) {
            val locationFragment =
                supportFragmentManager.findFragmentByTag(schedulePlaceTag) as SchedulingPlaceFragment
            locationFragment.updateLocations()
            page = 0
            setUpCurrentPage()
            supportFragmentManager.popBackStack(noMatchTag, 0)
            return
        }
        if (page < 2) page++
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (page == 1) {
            ft.replace(body_fragment.id, SchedulingTimeFragment(), scheduleTimeTag)
        } else {
            val timeFragment =
                supportFragmentManager.findFragmentByTag(scheduleTimeTag) as SchedulingTimeFragment
            timeFragment.updateSchedule()
            ft.replace(body_fragment.id, SchedulingPlaceFragment(), schedulePlaceTag)
        }
        setUpCurrentPage()
        ft.addToBackStack("ft")
        ft.commit()
    }

    private fun setUpCurrentPage() {
        if (page == 0) {
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            navigationView.itemIconTintList = null
            backButton.background = ContextCompat.getDrawable(this, R.drawable.ic_sign_in_logo)
            backButton.maxHeight = 40
            backButton.maxWidth = 40
            backButton.setOnClickListener {
                if (drawerLayout.isOpen) {
                    drawerLayout.close()
                } else {
                    drawerLayout.open()
                }
            }
            scheduling_header.text = getString(R.string.no_match_header)
            nextButton.text = getString(R.string.no_match_availability)
            nextButton.isEnabled = true
            nextButton.setPadding(100, 0, 100, 0)
        } else {
            backButton.background = ContextCompat.getDrawable(this, R.drawable.ic_back_carrot)
            nextButton.isEnabled = false
            nextButton.setPadding(180, 0, 180, 0)
            if (page == 1) {
                scheduling_header.text = getString(R.string.scheduling_time_header)
                nextButton.text = getString(R.string.scheduling_time_button)
            } else {
                scheduling_header.text = getString(R.string.scheduling_place_header)
                nextButton.text = getString(R.string.scheduling_place_button)
            }
        }
    }
}