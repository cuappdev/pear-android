package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.User
import com.cornellappdev.coffee_chats_android.models.UserField
import com.cornellappdev.coffee_chats_android.networking.Endpoint
import com.cornellappdev.coffee_chats_android.networking.Request
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnboardingActivity : AppCompatActivity(), OnFilledOutListener {
    private var content = Content.CREATE_PROFILE
    private lateinit var user: User
    private val navigationList =
        listOf(Content.CREATE_PROFILE, Content.INTERESTS, Content.GROUPS, Content.GOALS, Content.TALKING_POINTS, Content.SOCIAL_MEDIA)
    private val addLaterPages = listOf(Content.GROUPS, Content.GOALS, Content.TALKING_POINTS, Content.SOCIAL_MEDIA)


    enum class Content {
        CREATE_PROFILE,
        INTERESTS,
        GROUPS,
        GOALS,
        TALKING_POINTS,
        SOCIAL_MEDIA
    }

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        CoroutineScope(Dispatchers.Main).launch {
            val getUserEndpoint = Endpoint.getUser()
            val userTypeToken = object : TypeToken<ApiResponse<User>>() {}.type
            user = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<User>>(
                    getUserEndpoint.okHttpRequest(),
                    userTypeToken
                )
            }!!.data
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(body_fragment.id, CreateProfileFragment(), content.name)
                .addToBackStack("ft")
                .commit()
            setUpCurrentPage()
        }
        back_button.setOnClickListener { onBackPressed() }
        increaseHitArea(back_button)
        onboarding_next.setOnClickListener { onNextPage(it) }
        add_later.setOnClickListener { onNextPage(it) }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnFilledOutObservable) {
            fragment.setOnFilledOutListener(this)
        }
    }

    override fun onBackPressed() {
        if (content == navigationList.first()) {
            finish()
        } else {
            content = navigationList[navigationList.indexOf(content) - 1]
            supportFragmentManager.popBackStack()
            setUpCurrentPage()
        }
    }

    private fun onNextPage(view: View) {
        val currFragment =
            supportFragmentManager.findFragmentByTag(content.name) as OnFilledOutObservable
        currFragment.saveInformation()
        // hide keyboard
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        if (content != navigationList.last()) {
            content = navigationList[navigationList.indexOf(content) + 1]
            val fragment: Fragment = when (content) {
                Content.CREATE_PROFILE -> CreateProfileFragment()
                Content.INTERESTS -> UserFieldFragment.newInstance(UserField.Category.INTEREST)
                Content.GROUPS -> UserFieldFragment.newInstance(UserField.Category.GROUP)
                Content.GOALS -> UserFieldFragment.newInstance(UserField.Category.GOAL)
                Content.TALKING_POINTS -> UserFieldFragment.newInstance(UserField.Category.TALKING_POINT)
                Content.SOCIAL_MEDIA -> SocialMediaFragment()
            }
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(body_fragment.id, fragment, content.name).addToBackStack("ft").commit()
            setUpCurrentPage()
        } else {
            // onboarding done, launch SchedulingActivity
            val intent = Intent(this, SchedulingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    private fun setUpCurrentPage() {
        onboarding_header.text = when (content) {
            Content.CREATE_PROFILE -> getString(R.string.demographics_header, user.firstName)
            Content.INTERESTS -> getString(R.string.interests_header)
            Content.GROUPS -> getString(R.string.groups_header)
            Content.GOALS -> getString(R.string.goals_header)
            Content.TALKING_POINTS -> getString(R.string.talking_pointers_header)
            Content.SOCIAL_MEDIA -> getString(R.string.social_media_header)
        }
        back_button.visibility = if (content == Content.CREATE_PROFILE) View.GONE else View.VISIBLE
        add_later.visibility = if (content in addLaterPages) View.VISIBLE else View.GONE
        onboarding_next.text =
            if (content == navigationList.last()) getString(R.string.ready_for_pear) else getString(
                R.string.next
            )
    }

    override fun onFilledOut() {
        onboarding_next.isEnabled = true
    }

    override fun onSelectionEmpty() {
        onboarding_next.isEnabled = false
    }
}