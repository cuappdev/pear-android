package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.adapters.DayAdapter
import com.cornellappdev.coffee_chats_android.adapters.TimeOptionAdapter
import kotlinx.android.synthetic.main.activity_scheduling.*
import kotlinx.android.synthetic.main.fragment_scheduling_time.*


class SchedulingTimeFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_scheduling_time, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val timeAdapter =
            TimeOptionAdapter(
                context!!,
                times
            )
        time_gridview.adapter = timeAdapter
        var dayAdapter =
            DayAdapter(
                context!!,
                days
            )
        day_selection.adapter = dayAdapter

        day_header.text = "Every Sunday"        // Sunday by default
        for (day in daysFullName) {
            selectedTimes[day] = mutableListOf<String>()
        }

        var previousDot: ImageView? = null
        day_selection.onItemClickListener = OnItemClickListener { parent, v, position, id ->

            val daySelectedView = v as ConstraintLayout
            val daySelectedDot = daySelectedView.getChildAt(1) as ImageView //day indicator (small dot)

            val sundaySelectedView = day_selection.getChildAt(0) as ConstraintLayout
            val sundaySelectedDot = sundaySelectedView.getChildAt(1) as ImageView //small dot below Sunday

            //hide Sunday indicator when the first day is clicked
            if (previousDot == null) sundaySelectedDot.visibility = View.INVISIBLE
            //or hide the indicator of the last clicked day when a new day is clicked
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
                    timeTextView.background = getDrawable(context!!, R.drawable.selected_rounded_time_option)
                } else {
                    timeTextView.background = getDrawable(context!!, R.drawable.unselected_rounded_time_option)
                }
            }
        }

        time_gridview.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            val timeSelectedView = v as LinearLayout
            val timeSelectedTextView = timeSelectedView.getChildAt(0) as TextView
            val timeSelectedIndex = selectedTimes[currDay]!!.indexOf(timeSelectedTextView.text.toString())
            if (timeSelectedIndex > -1) {
                selectedTimes[currDay]!!.remove(timeSelectedTextView.text.toString())
                timeSelectedTextView.background = getDrawable(context!!, R.drawable.unselected_rounded_time_option)
                // change the day button to white if no time is selected for current day
                if (selectedTimes[currDay]!!.size == 0) {
                    selectedDays.remove(currDay)
                    val currDayIndex = daysFullName.indexOf(currDay)
                    val daySelectedView = day_selection.getChildAt(currDayIndex) as ConstraintLayout
                    currDayTextView = daySelectedView.getChildAt(0) as TextView
                    currDayTextView.background = getDrawable(context!!, R.drawable.unselected_scheduling_circle_button)
                }
                if (selectedDays.isEmpty()) callback!!.onSelectionEmpty()
            } else {
                selectedTimes[currDay]!!.add(timeSelectedTextView.text.toString())
                selectedDays.add(currDay)
                timeSelectedTextView.background = getDrawable(context!!, R.drawable.selected_rounded_time_option)
                // change day button to highlighted
                val currDayIndex = daysFullName.indexOf(currDay)
                val daySelectedView = day_selection.getChildAt(currDayIndex) as ConstraintLayout
                currDayTextView = daySelectedView.getChildAt(0) as TextView
                currDayTextView.background = getDrawable(context!!, R.drawable.selected_scheduling_circle_button)
                // enable finish button
                callback!!.onFilledOut()
            }
        }
    }

    private var callback: OnFilledOutListener? = null

    fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    interface OnFilledOutListener {
        fun onFilledOut()
        fun onSelectionEmpty()
    }
}