package com.cornellappdev.coffee_chats_android

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cornellappdev.coffee_chats_android.models.ClubOrInterest
import com.cornellappdev.coffee_chats_android.models.InternalStorage
import com.cornellappdev.coffee_chats_android.models.UserProfile
import kotlinx.android.synthetic.main.fragment_create_profile.*
import java.util.Locale.filter


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
        createProfileFragment.rootView.setBackgroundColor(
            resources.getColor(R.color.background_green)
        )

        selected = resources.getColor(R.color.onboardingListSelected)
        unselected = resources.getColor(R.color.onboarding_fields)

        currentPage = 1

        // reads in user profile
        profile = InternalStorage.readObject(this, "profile") as UserProfile

        header = findViewById(R.id.signup_header)
        nextButton = findViewById(R.id.signup_next)
        nextButton.setOnClickListener { onNextPage() }
        backButton = findViewById(R.id.signup_back)
        backButton.setText(R.string.go_back)
        backButton.setOnClickListener { onBackPage() }

        // nextButton is disabled until user has chosen at least one interest
        nextButton.isEnabled = false
        nextButton.isClickable = false

        for (i in interestTitles.indices) {
            interests[i] = ClubOrInterest(interestTitles[i], interestSubtitles[i])

            for (j in profile.interests.indices) {
                if (interestTitles[i] == profile.interests[j]) {
                    interests[i].setSelected()
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
                    clubs[i].setSelected()
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
            val drawableBox = selectedView.background
            val currObj = if (currentPage == 1) interests[position] else clubs[position]

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
                    profile.interests.remove(currObj.getText())
                    if (profile.interests.size == 0) {
                        nextButton.isEnabled = false
                        nextButton.isClickable = false
                    }
                } else {
                    profile.clubs.remove(currObj.getText())
                    if (profile.clubs.size == 0) {
                        nextButton.isEnabled = false
                        nextButton.isClickable = false
                    }
                }
            }
        }

        updatePage()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }
    fun updatePage() {
        when (currentPage) {
            1 -> {
                clubSearch.visibility = View.GONE
                adapter =
                    ClubInterestAdapter(
                        this,
                        interests,
                        false
                    )
                interestsAndClubs.adapter = adapter

                header.setText(R.string.interests_header)
                nextButton.setText(R.string.almost_there)

                for (i in 0 until adapter.count) {
                    Log.d("ClubInterestActivity", "current page = 1")
                    val v = interestsAndClubs.adapter.getView(i, null, interestsAndClubs)
                    val drawableBox = v.background
                    val interest = interests[i]

                    if (interest.isSelected())
                        drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                    else drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
                }

                if (profile.interests.size == 0) {
                    nextButton.isEnabled = false
                    nextButton.isClickable = false
                } else {
                    nextButton.isEnabled = true
                    nextButton.isClickable = true
                }
            }
            2 -> {
                clubSearch.visibility = View.VISIBLE
                adapter =
                    ClubInterestAdapter(
                        this,
                        clubs,
                        true
                    )
                interestsAndClubs.adapter = adapter

                // initialize searchview
                clubSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextChange(newText: String): Boolean {
                        Log.d("ClubInterestActivity", "newText: $newText")
                        adapter.filter.filter(newText)
                        Log.d("ClubInterestActivity", "count: " + adapter.count)
                        return true
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                })



                header.setText(R.string.clubs_header)
                nextButton.setText(R.string.get_started)

                Log.d("ClubInterestActivity", "count: " + adapter.count)
                for (i in 0 until adapter.count) {
                    val v = interestsAndClubs.adapter.getView(i, null, interestsAndClubs)
                    val drawableBox = v.background
                    val club = clubs[i]

                    if (club.isSelected())
                        drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                    else drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
                }
                if (profile.clubs.size == 0) {
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
        } else if (currentPage == 2) {
            currentPage -= 1
            updatePage()
        }
    }
}
