package com.example.coffee_chats_android

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.example.coffee_chats_android.Models.ClubOrInterest
import kotlinx.android.synthetic.main.fragment_create_profile.*

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
    var interests : Array<ClubOrInterest> = Array(interestTitles.size) { index -> ClubOrInterest("", "") }
    var clubs : Array<ClubOrInterest> = Array(clubTitles.size) { index -> ClubOrInterest("", "") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_create_profile)
        setContentView(R.layout.fragment_match)

        var lovesText = findViewById<TextView>(R.id.match_loves_text)
        var sharedClubsText = findViewById<TextView>(R.id.match_shared_clubs_text)
        var enjoysText = findViewById<TextView>(R.id.match_enjoys_text)
        var clubsText = findViewById<TextView>(R.id.match_clubs_text)
        var name = findViewById<TextView>(R.id.match_name)
        var major = findViewById<TextView>(R.id.match_major)
        var origin = findViewById<TextView>(R.id.match_origin)

        var enjoys = findViewById<TextView>(R.id.match_enjoys)
        var clubs = findViewById<TextView>(R.id.match_clubs)

        lovesText.text = "design and tech"
        sharedClubsText.text = "AppDev"
        enjoysText.text = "music, reading, and business"
        clubsText.text = "EzraBox"

        name.text = "Johnathan Anderson"
        major.text = "Government '20"
        origin.text = "From Ithaca, NY"

        enjoys.text = getString(R.string.match_enjoys, "He")
        clubs.text = getString(R.string.match_clubs, "He")

//        for (i in 0 until interestTitles.size) {
//            interests[i] = ClubOrInterest(interestTitles[i], interestSubtitles[i])
//        }
//
//        for (i in 0 until clubTitles.size) {
//            clubs[i] = ClubOrInterest(clubTitles[i], "")
//        }
//
//        header = findViewById(R.id.signup_header)
//        nextButton = findViewById(R.id.signup_next)
//        nextButton.setOnClickListener { view -> onNextPage() }
//        backButton = findViewById(R.id.signup_back)
//        backButton.setText(R.string.go_back)
//        backButton.setOnClickListener { view -> onBackPage() }
//
//        adapter = ClubInterestAdapter(this, interests)
//        interestsAndClubs = findViewById(R.id.interests_or_clubs)
//        interestsAndClubs.adapter = adapter
//
//        val selected = resources.getColor(R.color.selected_interest_or_club)
//        val unselected = resources.getColor(R.color.unselected_interest_or_club)
//        interestsAndClubs.setOnItemClickListener { parent, view, position, id ->
//            val selectedView = view.findViewById<ConstraintLayout>(R.id.club_or_interest_box)
//            val drawableBox = selectedView.background
//            interests[position].toggleSelected()
//            if (interests[position].isSelected()) drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
//            else drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
//        }
//
//        updatePage()
    }

    fun updatePage() {
        when (currentPage) {
            1 -> {
                adapter = ClubInterestAdapter(this, interests)
                interestsAndClubs.adapter = adapter
                header.setText(R.string.interests_header)
                nextButton.setText(R.string.almost_there)
            }
            2 -> {
                adapter = ClubInterestAdapter(this, clubs)
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
