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
import com.cornellappdev.coffee_chats_android.adapters.ClubInterestAdapter
import com.cornellappdev.coffee_chats_android.models.ClubOrInterest
import com.cornellappdev.coffee_chats_android.models.InternalStorage
import com.cornellappdev.coffee_chats_android.models.UserProfile
import kotlinx.android.synthetic.main.fragment_create_profile.*


class ClubInterestActivity : AppCompatActivity() {
    enum class CurrentPage {
        INTERESTS,
        CLUBS
    }
    var currentPage: CurrentPage = CurrentPage.INTERESTS
    lateinit var adapter: ClubInterestAdapter
    lateinit var profile: UserProfile
    val interestTitles = arrayOf("Art", "Business", "Design", "Humanities", "Fitness & Sports", "Tech", "More")
    val interestSubtitles =  arrayOf("painting crafts, embroidery", "finance, entrepreneurship, VC", "UX/UI, graphic, print",
        "history, politics", "working out, outdoors, basketball", "random technology", "there is more")
    val clubTitles = arrayOf("AppDev", "DTI", "Guac Magazine", "GCC", "CVC", "CVS")

    var selected = 0
    var unselected = 0

    var interests : Array<ClubOrInterest> = Array(interestTitles.size) { index ->
        ClubOrInterest("", "")
    }
    var clubs : Array<ClubOrInterest> = Array(clubTitles.size) { index ->
        ClubOrInterest("", "")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_create_profile)

        selected = resources.getColor(R.color.onboardingListSelected)
        unselected = resources.getColor(R.color.onboarding_fields)

        if(intent.getIntExtra("page", 1) == 1) {
            currentPage = CurrentPage.INTERESTS
            createProfileFragment.setBackgroundResource(R.drawable.onboarding_background_2)
        } else {
            currentPage = CurrentPage.CLUBS
            createProfileFragment.setBackgroundResource(R.drawable.onboarding_background_3)
        }


        // reads in user profile
        profile = InternalStorage.readObject(this, "profile") as UserProfile

        signup_next.setOnClickListener { onNextPage() }
        add_later.visibility = View.INVISIBLE
        add_later.setOnClickListener { onNextPage() }
        back_button.setOnClickListener { onBackPage() }
        // incease the hit area of back button
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

        for (i in interestTitles.indices) {
            interests[i] = ClubOrInterest(interestTitles[i], interestSubtitles[i])

            for (j in profile.interests.indices) {
                if (interestTitles[i] == profile.interests[j]) {
                    signup_next.isEnabled = true
                    break
                }
            }
        }

        for (i in clubTitles.indices) {
            clubs[i] = ClubOrInterest(clubTitles[i], "")

            for (j in profile.clubs.indices) {
                if (clubTitles[i] == profile.clubs[j]) {
                    signup_next.isEnabled = true
                    break
                }
            }
        }

        interests_or_clubs.setOnItemClickListener { parent, view, position, id ->
            val selectedView = view.findViewById<ConstraintLayout>(R.id.club_or_interest_box)
            val selectedText = selectedView.findViewById<TextView>(R.id.club_or_interest_text).text
            val drawableBox = selectedView.background
            val currObj = if (currentPage == CurrentPage.INTERESTS) interests[position] else clubs[clubTitles.indexOf(selectedText)]
            currObj.toggleSelected()
            if (currObj.isSelected()) {
                drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                if (currentPage == CurrentPage.INTERESTS) profile.interests.add(currObj.getText())
                else profile.clubs.add(currObj.getText())

                if (!signup_next.isEnabled) {
                    signup_next.isEnabled = true
                }
            } else {
                drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)

                if (currentPage == CurrentPage.INTERESTS) {
                    profile.interests.remove(selectedText)
                    if (profile.interests.isEmpty()) {
                        signup_next.isEnabled = false
                    }
                } else {
                    profile.clubs.remove(selectedText)
                    if (profile.clubs.isEmpty()) {
                        signup_next.isEnabled = false
                    }
                }
            }
        }

        updatePage()
    }

    fun updatePage() {
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

                    if (profile.interests.contains(name))
                        drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                    else
                        drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
                }

                signup_next.isEnabled = profile.interests.isNotEmpty()
            }
            CurrentPage.CLUBS -> {
                club_search.visibility = View.VISIBLE
                club_search.queryHint = "Search"

                val searchImgId =
                    resources.getIdentifier("android:id/search_button", null, null)
                val searchIcon: ImageView =
                    club_search.findViewById(searchImgId)
                searchIcon.setColorFilter(
                    resources.getColor(R.color.searchHint), PorterDuff.Mode.DARKEN
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
                        if (!newText.isBlank()) {
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

                signup_header.setText(R.string.clubs_header)
                signup_next.setText(R.string.get_started)
                add_later.visibility = View.VISIBLE

                for (i in 0 until adapter.count) {
                    val v = interests_or_clubs.adapter.getView(i, null, interests_or_clubs)
                    val drawableBox = v.background
                    val nameView = v.findViewById<TextView>(R.id.club_or_interest_text)
                    val name = nameView.text.toString()

                    if (profile.clubs.contains(name))
                        drawableBox.setColorFilter(selected, PorterDuff.Mode.MULTIPLY)
                    else
                        drawableBox.setColorFilter(unselected, PorterDuff.Mode.MULTIPLY)
                }

                signup_next.isEnabled = profile.clubs.isNotEmpty()
            }
        }
    }

    fun onNextPage() {
        InternalStorage.writeObject(this, "profile", profile as Object)

        if (currentPage == CurrentPage.INTERESTS) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            val intent = Intent(this, ClubInterestActivity::class.java)
            intent.putExtra("page", 2)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else if (currentPage == CurrentPage.CLUBS) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            // here fire up an intent to go to the page after onboarding
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun onBackPage() {
        InternalStorage.writeObject(this, "profile", profile as Object)

        if (currentPage == CurrentPage.INTERESTS) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            finish()
        } else if (currentPage == CurrentPage.CLUBS) {
            InternalStorage.writeObject(this, "profile", profile as Object)
            currentPage = CurrentPage.INTERESTS
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}