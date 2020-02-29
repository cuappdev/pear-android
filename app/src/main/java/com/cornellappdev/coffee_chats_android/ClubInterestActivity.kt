package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
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
    lateinit var backButton: Button
    lateinit var profile: UserProfile
    val interestTitles = arrayOf("Art", "Business", "Design", "Humanities", "Fitness & Sports", "Tech", "More")
    val interestSubtitles =  arrayOf("painting crafts, embroidery", "finance, entrepreneurship, VC", "UX/UI, graphic, print",
                                        "history, politics", "working out, outdoors, basketball", "random technology", "there is more")
    val clubTitles = arrayOf("AppDev", "DTI", "Guac Magazine", "GCC", "CVC", "CVS")
    lateinit var selectedView: ConstraintLayout

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
        createProfileFragment.rootView.setBackgroundColor(resources.getColor(
            R.color.background_green))

        selected = resources.getColor(R.color.onboardingButtonEnabled)
        unselected = resources.getColor(R.color.onboarding_fields)

        currentPage = 1

        // reads in user profile
        profile = InternalStorage.readObject(this, "profile") as UserProfile

        header = findViewById(R.id.signup_header)
        nextButton = findViewById(R.id.signup_next)
        nextButton.setOnClickListener { view -> onNextPage() }
        backButton = findViewById(R.id.signup_back)
        backButton.setText(R.string.go_back)
        backButton.setOnClickListener { view -> onBackPage() }

        // nextButton is disabled until user has chosen at least one interest
        nextButton.isEnabled = false
        nextButton.isClickable = false

        for (i in interestTitles.indices) {
            interests[i] =
                ClubOrInterest(
                    interestTitles[i],
                    interestSubtitles[i]
                )
        }

        for (i in clubTitles.indices) {
            clubs[i] =
                ClubOrInterest(
                    clubTitles[i],
                    ""
                )
        }

        adapter = ClubInterestAdapter(
            this,
            interests,
            false
        )
        interestsAndClubs = findViewById(R.id.interests_or_clubs)
        interestsAndClubs.adapter = adapter

        // sets the color of interest entries according to existing profile (cache)
        // not working
        for (i in 0 until interestsAndClubs.count) {
            var entry = getViewByPosition(i, interestsAndClubs)!!.findViewById<ConstraintLayout>(R.id.club_or_interest_box)
            if (profile.interests.contains(interests[i].getText())) {
                entry.background.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                Log.d("ClubInterestActivity", "set to selected")
                // here this print statement gets triggered, thus it is the setColorFilter that's not working
            } else entry.background.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
        }

        interestsAndClubs.setOnItemClickListener { _, view, position, _ ->
            selectedView = view.findViewById(R.id.club_or_interest_box)
            val drawableBox = selectedView.background
            interests[position].toggleSelected()
            if (interests[position].isSelected()) {
                drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                if (currentPage == 1) {
                    profile.interests.add(interests[position].getText())
                } else profile.clubs.add(interests[position].getText())
            } else {
                drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
                if (currentPage == 1) {
                    profile.interests.remove(interests[position].getText())
                } else profile.clubs.remove(interests[position].getText())
            }

            // disable or enable next button accordingly
            if (currentPage == 1 && profile.interests.isEmpty() ||
                currentPage == 2 && profile.clubs.isEmpty()) {
                nextButton.isEnabled = false
                nextButton.isClickable = false
            } else {
                nextButton.isEnabled = true
                nextButton.isClickable = true
            }
        }

        updatePage()
    }

    fun updatePage() {
        when (currentPage) {
            1 -> {
                adapter =
                    ClubInterestAdapter(
                        this,
                        interests,
                        false
                    )
                interestsAndClubs.adapter = adapter

//                Log.d("ClubInterestActivity", interestsAndClubs.count.toString())


                header.setText(R.string.interests_header)
                nextButton.setText(R.string.almost_there)
//
                // disable or enable nextButton depending on whether interests are empty
//                if (profile.interests.isEmpty()) {
//                    nextButton.isEnabled = false
//                    nextButton.isClickable = false
//                } else {
//                    nextButton.isEnabled = true
//                    nextButton.isClickable = true
//                }
            }
            2 -> {
                adapter =
                    ClubInterestAdapter(
                        this,
                        clubs,
                        true
                    )
                interestsAndClubs.adapter = adapter

//                for (i in 0 until interestsAndClubs.count) {
//                    var entry = interestsAndClubs.getChildAt(i) as ConstraintLayout
//                    entry.setOnClickListener {
//                        if (clubs[i].isSelected()) {
//                            entry.background.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
//                            selectedClubs.add(clubs[i].getText())
//                            nextButton.isEnabled = true
//                            nextButton.isClickable = true
//                        } else {
//                            entry.background.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
//                            selectedClubs.remove(interests[i].getText())
//                        }
//                    }
//                }

                header.setText(R.string.clubs_header)
                nextButton.setText(R.string.get_started)

//                // disable or enable nextButton depending on whether interests are empty
//                if (profile.clubs.isEmpty()) {
//                    nextButton.isEnabled = false
//                    nextButton.isClickable = false
//                } else {
//                    nextButton.isEnabled = true
//                    nextButton.isClickable = true
//                }
            }
        }
    }

    fun onNextPage() {
        if (currentPage == 1) {
            currentPage += 1
            updatePage()
        } else if (currentPage == 2) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            // here fire up an intent to go to the page after onboarding
        }
    }

    fun onBackPage() {
        if (currentPage == 1) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            val intent = Intent(this, CreateProfileActivity::class.java)
            startActivity(intent)
        }

        if (currentPage == 2) {
            currentPage -= 1
            updatePage()
        }
    }

    fun getViewByPosition(pos: Int, listView: ListView): View? {
        val firstListItemPosition = listView.firstVisiblePosition
        val lastListItemPosition = firstListItemPosition + listView.childCount - 1
        return if (pos < firstListItemPosition || pos > lastListItemPosition) {
            listView.adapter.getView(pos, null, listView)
        } else {
            val childIndex = pos - firstListItemPosition
            listView.getChildAt(childIndex)
        }
    }
}
