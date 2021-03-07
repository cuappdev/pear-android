package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cornellappdev.coffee_chats_android.adapters.ClubInterestAdapter
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.ClubOrInterest
import com.cornellappdev.coffee_chats_android.models.UserProfile
import com.cornellappdev.coffee_chats_android.networking.*
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_create_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ClubInterestActivity : AppCompatActivity() {
    enum class CurrentPage {
        INTERESTS,
        GROUPS
    }
    private var currentPage: CurrentPage = CurrentPage.INTERESTS
    lateinit var adapter: ClubInterestAdapter
    private val interestTitles = arrayOf("Art", "Business", "Dance", "Design", "Fashion", "Fitness & Sports", "Food", "Humanities", "Music", "Photography", "Reading", "Sustainability", "Tech", "Travel", "TV & Film")
    private val interestSubtitles =  arrayOf("painting, crafts, embroidery", "entrepreneurship, finance, VC", "urban, hip hop, ballet, swing",  "UX/UI, graphic, print",
        "fashion", "working out, outdoors, basketball", "cooking, eating, baking", "history, politics", "instruments, producing, acapella", "digital, analog", "reading", "sustainability", "programming, web/app development", "road, trips, backpacking")
    private lateinit var clubTitles: List<String>

    private lateinit var userInterests: ArrayList<String>
    private lateinit var userGroups: ArrayList<String>

    var selectedColor = 0
    var unselectedColor = 0

    private lateinit var interests : Array<ClubOrInterest>
    private lateinit var clubs : Array<ClubOrInterest>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_create_profile)

        selectedColor = ContextCompat.getColor(this, R.color.onboardingListSelected)
        unselectedColor = ContextCompat.getColor(this, R.color.onboarding_fields)

        if(intent.getIntExtra("page", 1) == 1) {
            currentPage = CurrentPage.INTERESTS
            createProfileFragment.setBackgroundResource(R.drawable.onboarding_background_2)
        } else {
            currentPage = CurrentPage.GROUPS
            createProfileFragment.setBackgroundResource(R.drawable.onboarding_background_3)
        }

        signup_next.setOnClickListener { onNextPage() }
        add_later.visibility = View.INVISIBLE
        add_later.setOnClickListener { onNextPage() }
        back_button.setOnClickListener { onBackPage() }
        // increase the hit area of back button
        val parent =
            back_button.parent as View // button: the view you want to enlarge hit area

        parent.post {
            val rect = Rect()
            back_button.getHitRect(rect)
            rect.top -= 100 // increase top hit area
            rect.left -= 100 // increase left hit area
            rect.bottom += 100 // increase bottom hit area
            rect.right += 100 // increase right hit area
            parent.touchDelegate = TouchDelegate(rect, back_button)
        }

        // signup_next is disabled until user has chosen at least one interest
        signup_next.isEnabled = false

        CoroutineScope(Dispatchers.Main).launch {
            val getUserInterestsEndpoint = Endpoint.getUserInterests()
            val interestTypeToken = object : TypeToken<ApiResponse<List<String>>>() {}.type
            userInterests = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<String>>>(
                    getUserInterestsEndpoint.okHttpRequest(),
                    interestTypeToken
                )
            }!!.data as ArrayList<String>
            interests = Array(interestTitles.size) {
                ClubOrInterest("", "")
            }
            for (i in interestTitles.indices) {
                interests[i] = ClubOrInterest(interestTitles[i], if (i < interestSubtitles.size) interestSubtitles[i] else "")

                for (j in userInterests.indices) {
                    if (interestTitles[i] == userInterests[j]) {
                        signup_next.isEnabled = true
                        break
                    }
                }
            }
            val getGroupsEndpoint = Endpoint.getAllGroups()
            val groupTypeToken = object : TypeToken<ApiResponse<List<String>>>() {}.type
            clubTitles = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<String>>>(
                    getGroupsEndpoint.okHttpRequest(),
                    groupTypeToken
                )!!.data
            }
            val getUserGroupsEndpoint = Endpoint.getUserGroups()
            userGroups = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<String>>>(
                    getUserGroupsEndpoint.okHttpRequest(),
                    groupTypeToken
                )
            }!!.data as ArrayList<String>
            clubs = Array(clubTitles.size) {
                ClubOrInterest("", "")
            }
            for (i in clubTitles.indices) {
                clubs[i] = ClubOrInterest(clubTitles[i], "")

                for (j in userGroups.indices) {
                    if (clubTitles[i] == userGroups[j]) {
                        signup_next.isEnabled = true
                        break
                    }
                }
            }

            interests_or_clubs.setOnItemClickListener { _, view, position, _ ->
                val selectedView = view.findViewById<ConstraintLayout>(R.id.club_or_interest_box)
                val selectedText = selectedView.findViewById<TextView>(R.id.club_or_interest_text).text
                val drawableBox = selectedView.background
                val currObj = if (currentPage == CurrentPage.INTERESTS) interests[position] else clubs[clubTitles.indexOf(selectedText)]
                currObj.toggleSelected()
                if (currObj.isSelected()) {
                    drawableBox.colorFilter = BlendModeColorFilter(selectedColor, BlendMode.MULTIPLY)
                    if (currentPage == CurrentPage.INTERESTS) userInterests.add(currObj.getText())
                    else userGroups.add(currObj.getText())

                    if (!signup_next.isEnabled) {
                        signup_next.isEnabled = true
                    }
                } else {
                    drawableBox.colorFilter = BlendModeColorFilter(unselectedColor, BlendMode.MULTIPLY)
                    if (currentPage == CurrentPage.INTERESTS) {
                        userInterests.remove(selectedText)
                        if (userInterests.isEmpty()) {
                            signup_next.isEnabled = false
                        }
                    } else {
                        userGroups.remove(selectedText)
                        if (userGroups.isEmpty()) {
                            signup_next.isEnabled = false
                        }
                    }
                }
            }

            updatePage()
        }
    }

    private fun updatePage() {
        when (currentPage) {
            CurrentPage.INTERESTS -> {
                club_search.visibility = View.GONE
                adapter =
                    ClubInterestAdapter(
                        this, interests, false
                    )
                interests_or_clubs.adapter = adapter

                signup_header.setText(R.string.interests_header)
                signup_next.setText(R.string.almost_there)
                add_later.visibility = View.INVISIBLE

                for (i in 0 until adapter.count) {
                    val v = interests_or_clubs.adapter.getView(i, null, interests_or_clubs)
                    val drawableBox = v.background
                    val nameView = v.findViewById<TextView>(R.id.club_or_interest_text)
                    val name = nameView.text.toString()

                    if (userInterests.contains(name))
                        drawableBox.colorFilter = BlendModeColorFilter(selectedColor, BlendMode.MULTIPLY)
                    else
                        drawableBox.colorFilter = BlendModeColorFilter(unselectedColor, BlendMode.MULTIPLY)
                }

                signup_next.isEnabled = userInterests.isNotEmpty()
            }
            CurrentPage.GROUPS -> {
                club_search.visibility = View.VISIBLE
                club_search.queryHint = "Search"

                val searchImgId =
                    resources.getIdentifier("android:id/search_button", null, null)
                val searchIcon: ImageView =
                    club_search.findViewById(searchImgId)
                searchIcon.setColorFilter(
                    ContextCompat.getColor(this, R.color.searchHint), PorterDuff.Mode.DARKEN
                )
                adapter =
                    ClubInterestAdapter(
                        this, clubs, true
                    )

                interests_or_clubs.adapter = adapter
                // initialize searchview
                club_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        var outputArr = clubs
                        if (newText.isNotBlank()) {
                            val filtered = clubs.filter {
                                it.getText().toLowerCase().contains(newText.toLowerCase())
                            }.toTypedArray()
                            outputArr = filtered
                        }
                        adapter =
                            ClubInterestAdapter(
                                applicationContext,
                                outputArr,
                                true
                            )
                        interests_or_clubs.adapter = adapter
                        for (i in 0 until adapter.count) {
                            val v = interests_or_clubs.adapter.getView(
                                i, null, interests_or_clubs
                            ) as ConstraintLayout
                            val drawableBox = v.background
                            val nameView = v.findViewById<TextView>(R.id.club_or_interest_text)
                            val name = nameView.text.toString()

                            if (userGroups.contains(name))
                                drawableBox.colorFilter = BlendModeColorFilter(selectedColor, BlendMode.MULTIPLY)
                            else
                                drawableBox.colorFilter = BlendModeColorFilter(unselectedColor, BlendMode.MULTIPLY)
                        }

                        return true
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                })

                signup_header.setText(R.string.clubs_header)
                signup_next.setText(R.string.get_started)
                add_later.visibility = View.VISIBLE

                for (i in 0 until adapter.count) {
                    val v = interests_or_clubs.adapter.getView(i, null, interests_or_clubs)
                    val drawableBox = v.background
                    val nameView = v.findViewById<TextView>(R.id.club_or_interest_text)
                    val name = nameView.text.toString()

                    if (userGroups.contains(name))
                        drawableBox.colorFilter = BlendModeColorFilter(selectedColor, BlendMode.MULTIPLY)
                    else
                        drawableBox.colorFilter = BlendModeColorFilter(unselectedColor, BlendMode.MULTIPLY)
                }

                signup_next.isEnabled = userGroups.isNotEmpty()
            }
        }
    }

    private fun onNextPage() {
        // TODO: Save interests or groups
        if (currentPage == CurrentPage.INTERESTS) {
            val intent = Intent(this, ClubInterestActivity::class.java)
            intent.putExtra("page", 2)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else if (currentPage == CurrentPage.GROUPS) {
            // onboarding done, clear all activities on top of SchedulingActivity and launch SchedulingActivity
            val intent = Intent(this, SchedulingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    private fun onBackPage() {
        // TODO: Save interests or groups?
        if (currentPage == CurrentPage.INTERESTS) {
            finish()
        } else if (currentPage == CurrentPage.GROUPS) {
            currentPage = CurrentPage.INTERESTS
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}