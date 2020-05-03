package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cornellappdev.coffee_chats_android.models.InternalStorage
import com.cornellappdev.coffee_chats_android.models.UserProfile
import kotlinx.android.synthetic.main.fragment_no_match.*

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_no_match)

        // /data/user/0/com.example.coffee_chats_android/files
//        Log.d("file directory", filesDir.toString())

        try {
            InternalStorage.readObject(this, "profile") as UserProfile
        } catch (e: Exception) {
            // no profile, meaning this app is used for the first time
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        no_match_availability.setOnClickListener {
            val intent = Intent(this, SchedulingActivity::class.java)
            startActivity(intent)
        }
    }
}