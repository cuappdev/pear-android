package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout


class SchedulingActivity : AppCompatActivity() {
    lateinit var currDay: String
    lateinit var currDayButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduling)

        val morningView = findViewById<ListView>(R.id.scheduling_morning)
        val afternoonView = findViewById<ListView>(R.id.scheduling_afternoon)
        val eveningView = findViewById<ListView>(R.id.scheduling_evening)

        val morningTimes = arrayOf("8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30")
        val afternoonTimes = arrayOf("12:00", "12:30", "1:00", "1:30", "2:00", "2:30", "3:00", "3:30")
        val eveningTimes = arrayOf("4:00", "4:30", "5:00", "5:30", "6:00", "6:30", "7:00", "7:30")

        val morningAdapter = TimeOptionAdapter(this, morningTimes)
        val afternoonAdapter = TimeOptionAdapter(this, afternoonTimes)
        val eveningAdapter = TimeOptionAdapter(this, eveningTimes)

        morningView.adapter = morningAdapter
        afternoonView.adapter = afternoonAdapter
        eveningView.adapter = eveningAdapter

        currDayButton = findViewById(R.id.sunday_button)
        currDay = currDayButton.tag.toString()

        val dayButtonsView = findViewById<ConstraintLayout>(R.id.day_selection)
        for (i in 0 until dayButtonsView.childCount) {
            val b: Button = dayButtonsView.getChildAt(i) as Button
            b.setOnClickListener {
                currDayButton = it as Button
                currDay = it.tag.toString()
            }
        }
    }
}