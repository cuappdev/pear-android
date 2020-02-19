package com.cornellappdev.coffee_chats_android

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cornellappdev.coffee_chats_android.Models.ClubOrInterest

class MainActivity : AppCompatActivity() {
    var currentPage = 1
    lateinit var header: TextView
    lateinit var adapter: ClubInterestAdapter
    lateinit var nextButton: Button
    lateinit var backButton: Button
    val interestTitles = arrayOf("Art", "Business", "Design", "Humanities", "Fitness & Sports", "Tech", "More")
    val interestSubtitles =  arrayOf("painting crafts, embroidery", "finance, entrepreneurship, VC", "UX/UI, graphic, print",
                                        "history, politics", "working out, outdoors, basketball", "random technology", "there is more")
    val clubTitles = arrayOf("AppDev", "DTI", "Guac Magazine", "GCC", "CVC", "CVS")

    lateinit var interestsAndClubs: ListView
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

        for (i in 0 until interestTitles.size) {
            interests[i] =
                ClubOrInterest(
                    interestTitles[i],
                    interestSubtitles[i]
                )
        }

        for (i in 0 until clubTitles.size) {
            clubs[i] =
                ClubOrInterest(
                    clubTitles[i],
                    ""
                )
        }

        header = findViewById(R.id.signup_header)
        nextButton = findViewById(R.id.signup_next)
        nextButton.setOnClickListener { view -> onNextPage() }
        backButton = findViewById(R.id.signup_back)
        backButton.setText(R.string.go_back)
        backButton.setOnClickListener { view -> onBackPage() }

        adapter = ClubInterestAdapter(
            this,
            interests
        )
        interestsAndClubs = findViewById(R.id.interests_or_clubs)
        interestsAndClubs.adapter = adapter

        val selected = resources.getColor(R.color.selected_interest_or_club)
        val unselected = resources.getColor(R.color.unselected_interest_or_club)
        interestsAndClubs.setOnItemClickListener { parent, view, position, id ->
            val selectedView = view.findViewById<ConstraintLayout>(R.id.club_or_interest_box)
            val drawableBox = selectedView.background
            interests[position].toggleSelected()
            if (interests[position].isSelected()) drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
            else drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
        }

        updatePage()
    }

    fun updatePage() {
        when (currentPage) {
            1 -> {
                adapter =
                    ClubInterestAdapter(
                        this,
                        interests
                    )
                interestsAndClubs.adapter = adapter
                header.setText(R.string.interests_header)
                nextButton.setText(R.string.almost_there)
            }
            2 -> {
                adapter =
                    ClubInterestAdapter(
                        this,
                        clubs
                    )
                interestsAndClubs.adapter = adapter
                header.setText(R.string.clubs_header)
                nextButton.setText(R.string.get_started)
            }
        }
    }

    fun onNextPage() {
        if (currentPage < 2) {
            currentPage += 1
            updatePage()
        }
    }

    fun onBackPage() {
        if (currentPage > 1) {
            currentPage -= 1
            updatePage()
        }
    }
}
