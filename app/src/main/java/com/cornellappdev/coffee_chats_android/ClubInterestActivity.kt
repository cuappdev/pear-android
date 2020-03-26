package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cornellappdev.coffee_chats_android.models.ClubOrInterest
import com.cornellappdev.coffee_chats_android.models.InternalStorage
import com.cornellappdev.coffee_chats_android.models.UserProfile
import kotlinx.android.synthetic.main.fragment_create_profile.*


class ClubInterestActivity : AppCompatActivity() {
    var currentPage = 1
    lateinit var header: TextView
    lateinit var adapter: ClubInterestAdapter
    lateinit var nextButton: Button
    lateinit var backButton: ImageButton
    lateinit var addLaterButton: Button
    lateinit var profile: UserProfile
    val interestTitles = arrayOf("Art", "Business", "Design", "Humanities", "Fitness & Sports", "Tech", "More")
    val interestSubtitles =  arrayOf("painting crafts, embroidery", "finance, entrepreneurship, VC", "UX/UI, graphic, print",
        "history, politics", "working out, outdoors, basketball", "random technology", "there is more")
    val clubTitles = arrayOf("AppDev", "DTI", "Guac Magazine", "GCC", "CVC", "CVS")

    lateinit var interestsAndClubs: ListView
    var selected = 0
    var unselected = 0

    var interests : Array<ClubOrInterest> = Array(interestTitles.size) { index ->
        ClubOrInterest(
            "",
            ""
        )
    }
    var clubs : Array<ClubOrInterest> = Array(clubTitles.size) { index ->
        ClubOrInterest(
            "",
            ""
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_create_profile)
        createProfileFragment.rootView.setBackgroundColor(
            resources.getColor(R.color.background_green)
        )

        selected = resources.getColor(R.color.onboardingListSelected)
        unselected = resources.getColor(R.color.onboarding_fields)

        if(intent.getIntExtra("page", 1) == 1) {
            currentPage = 1
            createProfileFragment.setBackgroundResource(R.drawable.onboarding_background_2)
        } else {
            currentPage = 2
            createProfileFragment.setBackgroundResource(R.drawable.onboarding_background_3)
        }


        // reads in user profile
        profile = InternalStorage.readObject(this, "profile") as UserProfile

        header = findViewById(R.id.signup_header)
        nextButton = findViewById(R.id.signup_next)
        nextButton.setOnClickListener { onNextPage() }
        addLaterButton = findViewById(R.id.add_later)
        addLaterButton.visibility = View.INVISIBLE
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { onBackPage() }
        // incease the hit area of back button
        val parent =
            backButton.parent as View // button: the view you want to enlarge hit area

        parent.post {
            val rect = Rect()
            backButton.getHitRect(rect)
            rect.top -= 100 // increase top hit area
            rect.left -= 100 // increase left hit area
            rect.bottom += 100 // increase bottom hit area
            rect.right += 100 // increase right hit area
            parent.touchDelegate = TouchDelegate(rect, backButton)
        }

        // nextButton is disabled until user has chosen at least one interest
        nextButton.isEnabled = false
        nextButton.isClickable = false

        for (i in interestTitles.indices) {
            interests[i] = ClubOrInterest(interestTitles[i], interestSubtitles[i])

            for (j in profile.interests.indices) {
                if (interestTitles[i] == profile.interests[j]) {
                    nextButton.isEnabled = true
                    nextButton.isClickable = true
                    break
                }
            }
        }

        for (i in clubTitles.indices) {
            clubs[i] = ClubOrInterest(clubTitles[i], "")

            for (j in profile.clubs.indices) {
                if (clubTitles[i] == profile.clubs[j]) {
                    nextButton.isEnabled = true
                    nextButton.isClickable = true
                    break
                }
            }
        }

        adapter = ClubInterestAdapter(this, interests, false)
        interestsAndClubs = findViewById(R.id.interests_or_clubs)
        interestsAndClubs.adapter = adapter

        interestsAndClubs.setOnItemClickListener { parent, view, position, id ->
            val selectedView = view.findViewById<ConstraintLayout>(R.id.club_or_interest_box)
            val selectedText = selectedView.findViewById<TextView>(R.id.club_or_interest_text).text
            val drawableBox = selectedView.background
            val currObj = if (currentPage == 1) interests[position] else clubs[clubTitles.indexOf(selectedText)]
            currObj.toggleSelected()
            if (currObj.isSelected()) {
                drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                if (currentPage == 1) profile.interests.add(currObj.getText())
                else profile.clubs.add(currObj.getText())

                if (!nextButton.isEnabled) {
                    nextButton.isEnabled = true
                    nextButton.isClickable = true
                }
            } else {
                drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)

                if (currentPage == 1) {
                    profile.interests.remove(selectedText)
                    if (profile.interests.isEmpty()) {
                        nextButton.isEnabled = false
                        nextButton.isClickable = false
                    }
                } else {
                    profile.clubs.remove(selectedText)
                    if (profile.clubs.isEmpty()) {
                        nextButton.isEnabled = false
                        nextButton.isClickable = false
                    }
                }
            }
        }

        updatePage()
    }

    fun updatePage() {
        when (currentPage) {
            1 -> {
                clubSearch.visibility = View.GONE
                adapter = ClubInterestAdapter(
                    this, interests, false
                )
                interestsAndClubs.adapter = adapter

                header.setText(R.string.interests_header)
                nextButton.setText(R.string.almost_there)
                addLaterButton.visibility = View.INVISIBLE

                for (i in 0 until adapter.count) {
                    val v = interestsAndClubs.adapter.getView(i, null, interestsAndClubs)
                    val drawableBox = v.background
                    val nameView = v.findViewById<TextView>(R.id.club_or_interest_text)
                    val name = nameView.text.toString()

                    if (profile.interests.contains(name))
                        drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                    else
                        drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
                }

                if (profile.interests.isEmpty()) {
                    nextButton.isEnabled = false
                    nextButton.isClickable = false
                } else {
                    nextButton.isEnabled = true
                    nextButton.isClickable = true
                }
            }
            2 -> {
                clubSearch.visibility = View.VISIBLE
                clubSearch.queryHint = "Search"

                val searchImgId =
                    resources.getIdentifier("android:id/search_button", null, null)
                val searchIcon: ImageView =
                    clubSearch.findViewById(searchImgId)
                searchIcon.setColorFilter(
                    resources.getColor(R.color.searchHint), PorterDuff.Mode.DARKEN
                )
                adapter = ClubInterestAdapter(
                    this, clubs, true
                )

                interestsAndClubs.adapter = adapter
                // initialize searchview
                clubSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        var outputArr = clubs
                        if (!newText.isBlank()) {
                            val filtered = clubs.filter {
                                it.getText().toLowerCase().contains(newText.toLowerCase())
                            }.toTypedArray()
                            outputArr = filtered
                        }
                        adapter = ClubInterestAdapter(applicationContext, outputArr, true)
                        interestsAndClubs.adapter = adapter
                        for (i in 0 until adapter.count) {
                            val v = interestsAndClubs.adapter.getView(
                                i, null, interestsAndClubs
                            ) as ConstraintLayout
                            val drawableBox = v.background
                            val nameView = v.findViewById<TextView>(R.id.club_or_interest_text)
                            val name = nameView.text.toString()

                            if (profile.clubs.contains(name))
                                drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                            else
                                drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
                        }

                        return true
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                })

                header.setText(R.string.clubs_header)
                nextButton.setText(R.string.get_started)
                addLaterButton.visibility = View.VISIBLE

                for (i in 0 until adapter.count) {
                    val v = interestsAndClubs.adapter.getView(i, null, interestsAndClubs)
                    val drawableBox = v.background
                    val nameView = v.findViewById<TextView>(R.id.club_or_interest_text)
                    val name = nameView.text.toString()

                    if (profile.clubs.contains(name))
                        drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                    else
                        drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
                }

                if (profile.clubs.isEmpty()) {
                    nextButton.isEnabled = false
                    nextButton.isClickable = false
                } else {
                    nextButton.isEnabled = true
                    nextButton.isClickable = true
                }
            }
        }
    }

    fun onNextPage() {
        InternalStorage.writeObject(this, "profile", profile as Object)

        if (currentPage == 1) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            val intent = Intent(this, ClubInterestActivity::class.java)
            intent.putExtra("page", 2)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else if (currentPage == 2) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            // here fire up an intent to go to the page after onboarding
        }
    }

    fun onBackPage() {
        InternalStorage.writeObject(this, "profile", profile as Object)

        if (currentPage == 1) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            finish()
        } else if (currentPage == 2) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            currentPage -= 1
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
