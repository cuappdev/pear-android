package com.example.coffee_chats_android

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}
