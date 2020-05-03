package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_no_match.*

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_no_match)

        no_match_availability.setOnClickListener {
            val intent = Intent(this, SchedulingActivity::class.java)
            startActivity(intent)
        }
    }
}