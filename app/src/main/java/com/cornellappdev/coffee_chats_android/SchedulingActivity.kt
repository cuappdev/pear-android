package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cornellappdev.coffee_chats_android.models.*
import com.cornellappdev.coffee_chats_android.networking.Endpoint
import com.cornellappdev.coffee_chats_android.networking.Request
import com.cornellappdev.coffee_chats_android.networking.refreshSession
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_scheduling.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class SchedulingActivity:
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
                    val refreshSessionEndpoint = Endpoint.refreshSession(preferencesHelper.refreshToken!!)
                    val typeToken = object : TypeToken<ApiResponse<UserSession>>() {}.type
                    val userSession = withContext(Dispatchers.IO) {
                        Request.makeRequest<ApiResponse<UserSession>>(refreshSessionEndpoint.okHttpRequest(), typeToken)
                    }!!.data
                    preferencesHelper.accessToken = userSession.accessToken
                    preferencesHelper.refreshToken = userSession.refreshToken
                    preferencesHelper.expiresAt = userSession.sessionExpiration.toLong()
                }
            }
            profile = InternalStorage.readObject(this, "profile") as UserProfile
        } else {
            // prompt user to log in
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        // add fragment to body_fragment
        ft.add(body_fragment.id, NoMatchFragment())
        ft.commit()

        back_button.setOnClickListener { onBackPage() }
        back_button.visibility = View.GONE

        scheduling_finish.setOnClickListener {onNextPage()}

        nextButton = findViewById(R.id.scheduling_finish)
        backButton = findViewById(R.id.back_button)
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
        if (page == 0) {
            backButton.visibility = View.GONE
            scheduling_header.text = getString(R.string.no_match_header)
            nextButton.text = getString(R.string.no_match_availability)
            nextButton.isEnabled = true
            nextButton.setPadding(100,0,100,0)
        } else if (page == 1) {
            scheduling_header.text = getString(R.string.scheduling_time_header)
            scheduling_finish.isEnabled = false
            profile = InternalStorage.readObject(this, "profile") as UserProfile
            for ((_, selectedTimes) in profile.availableTimes) {
                if (selectedTimes.isNotEmpty()) nextButton.isEnabled = true
            }
        }
    }

    private fun onNextPage() {
        if (page == 2) return
        back_button.visibility = View.VISIBLE
        if (page < 2) page++
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        nextButton.text = getString(R.string.scheduling_finish)
        nextButton.isEnabled = false
        nextButton.setPadding(180, 0, 180, 0)
        if (page == 1) {
            scheduling_header.text = getString(R.string.scheduling_time_header)
            ft.replace(body_fragment.id, SchedulingTimeFragment())
            profile = InternalStorage.readObject(this, "profile") as UserProfile
            for ((_, selectedTimes) in profile.availableTimes) {
                if (selectedTimes.isNotEmpty()) nextButton.isEnabled = true
            }
        } else {
            scheduling_header.text = getString(R.string.scheduling_place_header)
            ft.replace(body_fragment.id, SchedulingPlaceFragment())
            profile = InternalStorage.readObject(this, "profile") as UserProfile
            if (profile.preferredLocations.isNotEmpty()) nextButton.isEnabled = true
        }
        ft.addToBackStack("ft")
        ft.commit()
    }
}