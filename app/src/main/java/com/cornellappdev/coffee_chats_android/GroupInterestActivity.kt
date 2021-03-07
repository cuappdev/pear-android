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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cornellappdev.coffee_chats_android.adapters.GroupInterestAdapter
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.GroupOrInterest
import com.cornellappdev.coffee_chats_android.networking.*
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_create_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GroupInterestActivity : AppCompatActivity() {
    enum class CurrentPage {
        INTERESTS,
        GROUPS
    }

    private var currentPage: CurrentPage = CurrentPage.INTERESTS
    lateinit var adapter: GroupInterestAdapter
    private val interestTitles = arrayOf(
        "Art",
        "Business",
        "Dance",
        "Design",
        "Fashion",
        "Fitness & Sports",
        "Food",
        "Humanities",
        "Music",
        "Photography",
        "Reading",
        "Sustainability",
        "Tech",
        "Travel",
        "TV & Film"
    )
    private val interestSubtitles = arrayOf(
        "painting, crafts, embroidery",
        "entrepreneurship, finance, VC",
        "urban, hip hop, ballet, swing",
        "UX/UI, graphic, print",
        "fashion",
        "working out, outdoors, basketball",
        "cooking, eating, baking",
        "history, politics",
        "instruments, producing, acapella",
        "digital, analog",
        "reading",
        "sustainability",
        "programming, web/app development",
        "road, trips, backpacking"
    )
    private lateinit var groupTitles: Array<String>

    private lateinit var userInterests: ArrayList<String>
    private lateinit var userGroups: ArrayList<String>

    var selectedColor = 0
    var unselectedColor = 0

    private lateinit var interests: Array<GroupOrInterest>
    private lateinit var groups: Array<GroupOrInterest>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_create_profile)

        selectedColor = ContextCompat.getColor(this, R.color.onboardingListSelected)
        unselectedColor = ContextCompat.getColor(this, R.color.onboarding_fields)

        if (intent.getIntExtra("page", 1) == 1) {
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
            if (currentPage == CurrentPage.INTERESTS) {
                val getUserInterestsEndpoint = Endpoint.getUserInterests()
                val interestTypeToken = object : TypeToken<ApiResponse<List<String>>>() {}.type
                userInterests = withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<List<String>>>(
                        getUserInterestsEndpoint.okHttpRequest(),
                        interestTypeToken
                    )
                }!!.data as ArrayList<String>
                interests = Array(interestTitles.size) {
                    GroupOrInterest("", "")
                }
                for (i in interestTitles.indices) {
                    interests[i] = GroupOrInterest(
                        interestTitles[i],
                        if (i < interestSubtitles.size) interestSubtitles[i] else ""
                    )

                    for (j in userInterests.indices) {
                        if (interestTitles[i] == userInterests[j]) {
                            signup_next.isEnabled = true
                            break
                        }
                    }
                }
            } else {
                val getGroupsEndpoint = Endpoint.getAllGroups()
                val groupTypeToken = object : TypeToken<ApiResponse<List<String>>>() {}.type
                groupTitles = withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<List<String>>>(
                        getGroupsEndpoint.okHttpRequest(),
                        groupTypeToken
                    )!!.data.toTypedArray()
                }
                val getUserGroupsEndpoint = Endpoint.getUserGroups()
                userGroups = withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<List<String>>>(
                        getUserGroupsEndpoint.okHttpRequest(),
                        groupTypeToken
                    )
                }!!.data as ArrayList<String>
                groups = Array(groupTitles.size) {
                    GroupOrInterest("", "")
                }
                for (i in groupTitles.indices) {
                    groups[i] = GroupOrInterest(groupTitles[i], "")

                    for (j in userGroups.indices) {
                        if (groupTitles[i] == userGroups[j]) {
                            signup_next.isEnabled = true
                            break
                        }
                    }
                }
            }
            updatePage()
            interests_or_groups.setOnItemClickListener { _, view, position, _ ->
                val selectedView = view.findViewById<ConstraintLayout>(R.id.group_or_interest_box)
                val selectedText =
                    selectedView.findViewById<TextView>(R.id.group_or_interest_text).text
                val drawableBox = selectedView.background
                val currObj =
                    if (currentPage == CurrentPage.INTERESTS) interests[position] else groups[groupTitles.indexOf(
                        selectedText
                    )]
                currObj.toggleSelected()
                if (currObj.isSelected()) {
                    drawableBox.colorFilter =
                        BlendModeColorFilter(selectedColor, BlendMode.MULTIPLY)
                    if (currentPage == CurrentPage.INTERESTS) userInterests.add(currObj.getText())
                    else userGroups.add(currObj.getText())

                    if (!signup_next.isEnabled) {
                        signup_next.isEnabled = true
                    }
                } else {
                    drawableBox.colorFilter =
                        BlendModeColorFilter(unselectedColor, BlendMode.MULTIPLY)
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
        }
    }

    private fun updatePage() {
        when (currentPage) {
            CurrentPage.INTERESTS -> {
                group_search.visibility = View.GONE
                adapter =
                    GroupInterestAdapter(
                        this, interests, false
                    )
                interests_or_groups.adapter = adapter

                signup_header.setText(R.string.interests_header)
                signup_next.setText(R.string.almost_there)
                add_later.visibility = View.INVISIBLE

                for (i in interestTitles.indices) {
                    if (userInterests.contains(interestTitles[i])) {
                        interests[i].setSelected()
                    }
                }

                signup_next.isEnabled = userInterests.isNotEmpty()
            }
            CurrentPage.GROUPS -> {
                group_search.visibility = View.VISIBLE
                group_search.queryHint = "Search"

                val searchImgId =
                    resources.getIdentifier("android:id/search_button", null, null)
                val searchIcon: ImageView =
                    group_search.findViewById(searchImgId)
                searchIcon.setColorFilter(
                    ContextCompat.getColor(this, R.color.searchHint), PorterDuff.Mode.DARKEN
                )
                adapter =
                    GroupInterestAdapter(
                        this, groups, true
                    )

                interests_or_groups.adapter = adapter
                // initialize searchview
                group_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        var outputArr = groups
                        if (newText.isNotBlank()) {
                            val filtered = groups.filter {
                                it.getText().toLowerCase().contains(newText.toLowerCase())
                            }.toTypedArray()
                            outputArr = filtered
                        }
                        adapter =
                            GroupInterestAdapter(
                                applicationContext,
                                outputArr,
                                true
                            )
                        interests_or_groups.adapter = adapter
                        for (i in groupTitles.indices) {
                            if (userGroups.contains(groupTitles[i])) {
                                groups[i].setSelected()
                            }
                        }
                        return true
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                })

                signup_header.setText(R.string.groups_header)
                signup_next.setText(R.string.get_started)
                add_later.visibility = View.VISIBLE

                for (i in groupTitles.indices) {
                    if (userGroups.contains(groupTitles[i])) {
                        groups[i].setSelected()
                    }
                }

                signup_next.isEnabled = userGroups.isNotEmpty()
            }
        }
    }

    private fun onNextPage() {
        // update user interests or groups in backend
        CoroutineScope(Dispatchers.Main).launch {
            val updateEndpoint =
                when (currentPage) {
                    CurrentPage.INTERESTS -> Endpoint.updateInterests(userInterests)
                    CurrentPage.GROUPS -> Endpoint.updateGroups(userGroups)
                }
            val typeToken = object : TypeToken<ApiResponse<String>>() {}.type
            val updateResponse = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<String>>(
                    updateEndpoint.okHttpRequest(),
                    typeToken
                )
            }
            if (updateResponse == null || !updateResponse.success) {
                Toast.makeText(applicationContext, "Failed to save information", Toast.LENGTH_LONG)
                    .show()
            }
        }
        if (currentPage == CurrentPage.INTERESTS) {
            val intent = Intent(this, GroupInterestActivity::class.java)
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