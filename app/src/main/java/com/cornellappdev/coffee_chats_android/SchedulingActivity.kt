package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_scheduling.*
import android.widget.AdapterView.OnItemClickListener
import kotlinx.android.synthetic.main.activity_scheduling.back_button
import kotlinx.android.synthetic.main.fragment_create_profile.*


class SchedulingActivity : AppCompatActivity() {
    var currDay: String = "Sunday"
    lateinit var currDayTextView: TextView
    private val days = arrayOf("Su", "M", "Tu", "W", "Th", "F", "Sa")
    private val daysFullName = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday")
    private val times = arrayOf("9:00", "1:00", "5:00", "9:30", "1:30", "5:30", "10:00", "2:00",
    "6:00", "10:30", "2:30", "6:30", "11:00", "3:00", "7:00", "11:30", "3:30", "7:30", "12:00",
    "4:00", "8:00", "12:30", "4:30", "8:30")
    var selectedTimes  = HashMap<String, MutableList<String>>()
    var selectedDays = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduling)

        val timeAdapter = TimeOptionAdapter(this, times)
        time_gridview.adapter = timeAdapter
        var dayAdapter = DayAdapter(this, days)
        day_selection.adapter = dayAdapter

        day_header.text = "Every Sunday"        // Sunday by default
        for (day in daysFullName) {
            selectedTimes[day] = mutableListOf<String>()
        }

        back_button.setOnClickListener { finish() }

        var previousDot: ImageView? = null
        day_selection.onItemClickListener = OnItemClickListener { parent, v, position, id ->
//            val daySelectedIndex = dayAdapter.selectedPositions.indexOf(position)
            val daySelectedView = day_selection.getChildAt(position) as ConstraintLayout
            val daySelectedTextView = daySelectedView.getChildAt(0) as TextView
            val daySelectedDot = daySelectedView.getChildAt(1) as ImageView
            val sundaySelectedView = day_selection.getChildAt(0) as ConstraintLayout
            val sundaySelectedDot = sundaySelectedView.getChildAt(1) as ImageView
            if (previousDot == null) sundaySelectedDot.visibility = View.INVISIBLE
            if (previousDot != null) previousDot!!.visibility = View.INVISIBLE
            daySelectedDot.visibility = View.VISIBLE
            previousDot = daySelectedDot
            day_header.text = "Every " + daysFullName[position]
            currDay = daysFullName[position]
            // update the time gridview to reflect selected time slots
            for (i in times.indices) {
                val timeView = time_gridview.getChildAt(i) as LinearLayout
                val timeTextView = timeView.getChildAt(0) as TextView

                if (selectedTimes[currDay]!!.contains(times[i])) {
                    timeTextView.background = getDrawable(R.drawable.selected_rounded_time_option)
                } else {
                    timeTextView.background = getDrawable(R.drawable.unselected_rounded_time_option)
                }
            }
        }

        time_gridview.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            val timeSelectedView = time_gridview.getChildAt(position) as LinearLayout
            val timeSelectedTextView = timeSelectedView.getChildAt(0) as TextView
            val timeSelectedIndex = selectedTimes[currDay]!!.indexOf(timeSelectedTextView.text.toString())
            if (timeSelectedIndex > -1) {
                selectedTimes[currDay]!!.remove(timeSelectedTextView.text.toString())
                timeSelectedTextView.background = getDrawable(R.drawable.unselected_rounded_time_option)
                // change the day button to white if no time is selected for current day
                if (selectedTimes[currDay]!!.size == 0) {
                    selectedDays.remove(currDay)
                    val currDayIndex = daysFullName.indexOf(currDay)
                    val daySelectedView = day_selection.getChildAt(currDayIndex) as ConstraintLayout
                    currDayTextView = daySelectedView.getChildAt(0) as TextView
                    currDayTextView.background = getDrawable(R.drawable.unselected_scheduling_circle_button)
                }
                if (selectedDays.isEmpty()) scheduling_finish.isEnabled = false
            } else {
                selectedTimes[currDay]!!.add(timeSelectedTextView.text.toString())
                selectedDays.add(currDay)
                timeSelectedTextView.background = getDrawable(R.drawable.selected_rounded_time_option)
                // change day button to highlighted
                val currDayIndex = daysFullName.indexOf(currDay)
                val daySelectedView = day_selection.getChildAt(currDayIndex) as ConstraintLayout
                currDayTextView = daySelectedView.getChildAt(0) as TextView
                currDayTextView.background = getDrawable(R.drawable.selected_scheduling_circle_button)
                // enable finish button
                scheduling_finish.isEnabled = true
            }
        }


    }
}