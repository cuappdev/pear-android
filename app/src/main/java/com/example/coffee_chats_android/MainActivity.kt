package com.example.coffee_chats_android

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_create_profile.*

class MainActivity : AppCompatActivity() {
    var currentPage = 1
    lateinit var header: TextView
    lateinit var adapter: ClubInterestAdapter
    lateinit var nextButton: Button
    lateinit var backButton: Button
    val interests = arrayOf("Art", "Business", "Design", "Humanities", "Fitness & Sports", "Tech", "More")
    val clubs = arrayOf("AppDev", "DTI", "Guac Magazine", "GCC", "CVC", "CVS")
    lateinit var interestsAndClubs: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_create_profile)

        header = findViewById(R.id.signup_header)
        nextButton = findViewById(R.id.signup_next)
        nextButton.setOnClickListener { view -> onNextPage() }
        backButton = findViewById(R.id.signup_back)
        backButton.setText(R.string.go_back)
        backButton.setOnClickListener { view -> onBackPage() }

        adapter = ClubInterestAdapter(this, interests)
        interestsAndClubs = findViewById(R.id.interests_or_clubs)

        updatePage()
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
