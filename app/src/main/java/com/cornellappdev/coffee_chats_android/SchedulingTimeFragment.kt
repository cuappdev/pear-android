package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.adapters.DayAdapter
import com.cornellappdev.coffee_chats_android.adapters.TimeOptionAdapter
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.Availability
import com.cornellappdev.coffee_chats_android.networking.Endpoint
import com.cornellappdev.coffee_chats_android.networking.Request
import com.cornellappdev.coffee_chats_android.networking.getUserAvailabilities
import com.cornellappdev.coffee_chats_android.networking.updateAvailabilities
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_scheduling_time.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor


class SchedulingTimeFragment : Fragment() {
    private lateinit var currDay: String
    private lateinit var currDayTextView: TextView
    private lateinit var days: Array<String>
    private lateinit var daysFullName: Array<String>
    private lateinit var times: Array<String>
    private var selectedDays = mutableSetOf<String>()
    private val availableTimes = HashMap<String, MutableList<String>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_scheduling_time, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // initialize lateinit variables
        days = resources.getStringArray(R.array.days_abbr)
        daysFullName = resources.getStringArray(R.array.days_full_name)
        currDay = daysFullName[0]
        times = resources.getStringArray(R.array.times)
        for (day in daysFullName) {
            availableTimes[day] = mutableListOf()
        }

        // fetch existing user availabilities
        CoroutineScope(Dispatchers.Main).launch {
            val getUserAvailabilitiesEndpoint = Endpoint.getUserAvailabilities()
            val typeToken = object : TypeToken<ApiResponse<List<Availability>>>() {}.type
            val availabilities = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<Availability>>>(
                    getUserAvailabilitiesEndpoint.okHttpRequest(),
                    typeToken
                )
            }!!.data
            for ((day, times) in availabilities) {
                availableTimes[day]!!.addAll(times.map { t -> doubleTimeToString(t) })
                if (times.isNotEmpty()) {
                    selectedDays.add(day)
                }
            }

            if (selectedDays.isNotEmpty()) {
                callback!!.onFilledOut()
            }

            val timeAdapter =
                TimeOptionAdapter(
                    requireContext(),
                    times,
                    availableTimes[currDay]!!
                )
            time_gridview.adapter = timeAdapter

            val dayAdapter =
                DayAdapter(
                    requireContext(),
                    days,
                    daysFullName,
                    selectedDays
                )
            day_selection.adapter = dayAdapter
            day_header.text = getString(
                R.string.day_header,
                daysFullName[0].capitalize()
            )
            var previousDot: ImageView? = null
            day_selection.onItemClickListener = OnItemClickListener { _, v, position, _ ->
                val daySelectedView = v as ConstraintLayout
                val daySelectedDot =
                    daySelectedView.getChildAt(1) as ImageView // day indicator (small dot)

                val sundaySelectedView = day_selection.getChildAt(0) as ConstraintLayout
                val sundaySelectedDot =
                    sundaySelectedView.getChildAt(1) as ImageView // small dot below Sunday

                //hide Sunday indicator when the first day is clicked
                if (previousDot == null) sundaySelectedDot.visibility = View.INVISIBLE
                //or hide the indicator of the last clicked day when a new day is clicked
                if (previousDot != null) previousDot!!.visibility = View.INVISIBLE

                daySelectedDot.visibility = View.VISIBLE
                previousDot = daySelectedDot
                day_header.text =
                    getString(R.string.day_header, daysFullName[position].capitalize())
                currDay = daysFullName[position]
                // update the time gridview to reflect selected time slots
                for (i in times.indices) {
                    val timeView = time_gridview.getChildAt(i) as LinearLayout
                    val timeTextView = timeView.getChildAt(0) as TextView

                    timeTextView.background =
                        if (availableTimes[currDay]!!.contains(times[i])) {
                            getDrawable(requireContext(), R.drawable.selected_rounded_time_option)
                        } else {
                            getDrawable(requireContext(), R.drawable.unselected_rounded_time_option)
                        }
                }
            }

            time_gridview.onItemClickListener = OnItemClickListener { _, v, _, _ ->
                val timeSelectedView = v as LinearLayout
                val timeSelectedTextView = timeSelectedView.getChildAt(0) as TextView
                val timeSelectedIndex =
                    availableTimes[currDay]!!.indexOf(timeSelectedTextView.text.toString())
                if (timeSelectedIndex > -1) {
                    availableTimes[currDay]!!.remove(timeSelectedTextView.text.toString())
                    timeSelectedTextView.background =
                        getDrawable(requireContext(), R.drawable.unselected_rounded_time_option)
                    // change the day button to white if no time is selected for current day
                    if (availableTimes[currDay]!!.size == 0) {
                        selectedDays.remove(currDay)
                        val currDayIndex = daysFullName.indexOf(currDay)
                        val daySelectedView =
                            day_selection.getChildAt(currDayIndex) as ConstraintLayout
                        currDayTextView = daySelectedView.getChildAt(0) as TextView
                        currDayTextView.background = getDrawable(
                            requireContext(),
                            R.drawable.unselected_scheduling_circle_button
                        )
                    }
                    if (selectedDays.isEmpty()) callback!!.onSelectionEmpty()
                } else {
                    availableTimes[currDay]!!.add(timeSelectedTextView.text.toString())
                    selectedDays.add(currDay)
                    timeSelectedTextView.background =
                        getDrawable(requireContext(), R.drawable.selected_rounded_time_option)
                    // change day button to highlighted
                    val currDayIndex = daysFullName.indexOf(currDay)
                    val daySelectedView = day_selection.getChildAt(currDayIndex) as ConstraintLayout
                    currDayTextView = daySelectedView.getChildAt(0) as TextView
                    currDayTextView.background =
                        getDrawable(requireContext(), R.drawable.selected_scheduling_circle_button)
                    // enable finish button
                    callback!!.onFilledOut()
                }
            }
        }

//        val profile = InternalStorage.readObject(requireContext(), "profile") as UserProfile
//        // initialize [profile.availableTimes] HashMap and selectedDays based on existing profile
//        for (day in days) {
//            if (profile.availableTimes[day] == null) {
//                profile.availableTimes[day] = mutableListOf()
//            }
//            if (profile.availableTimes[day]!!.size != 0) selectedDays.add(day)
//        }
//        InternalStorage.writeObject(requireContext(), "profile", profile as Object)

//        val timeAdapter =
//            TimeOptionAdapter(
//                requireContext(),
//                times,
//                profile.availableTimes["Su"]!!
//            )
//        time_gridview.adapter = timeAdapter
//        val dayAdapter =
//            DayAdapter(
//                requireContext(),
//                days,
//                selectedDays
//            )
//        day_selection.adapter = dayAdapter
//        day_header.text = resources.getString(R.string.day_header, daysFullName[0].capitalize())        // Sunday by default
//
//        var previousDot: ImageView? = null
//        day_selection.onItemClickListener = OnItemClickListener { _, v, position, _ ->
//
//            val daySelectedView = v as ConstraintLayout
//            val daySelectedDot =
//                daySelectedView.getChildAt(1) as ImageView //day indicator (small dot)
//
//            val sundaySelectedView = day_selection.getChildAt(0) as ConstraintLayout
//            val sundaySelectedDot =
//                sundaySelectedView.getChildAt(1) as ImageView //small dot below Sunday
//
//            //hide Sunday indicator when the first day is clicked
//            if (previousDot == null) sundaySelectedDot.visibility = View.INVISIBLE
//            //or hide the indicator of the last clicked day when a new day is clicked
//            if (previousDot != null) previousDot!!.visibility = View.INVISIBLE
//
//            daySelectedDot.visibility = View.VISIBLE
//            previousDot = daySelectedDot
//            day_header.text = resources.getString(R.string.day_header, daysFullName[position].capitalize())
////            day_header.text = "Every " + daysFullName[position]
//            currDay = days[position]
//            // update the time gridview to reflect selected time slots
//            for (i in times.indices) {
//                val timeView = time_gridview.getChildAt(i) as LinearLayout
//                val timeTextView = timeView.getChildAt(0) as TextView
//
//                timeTextView.background =
//                    if (profile.availableTimes[currDay]!!.contains(times[i])) {
//                        getDrawable(requireContext(), R.drawable.selected_rounded_time_option)
//                    } else {
//                        getDrawable(requireContext(), R.drawable.unselected_rounded_time_option)
//                    }
//            }
//        }
//
//        time_gridview.onItemClickListener = OnItemClickListener { _, v, _, _ ->
//            val timeSelectedView = v as LinearLayout
//            val timeSelectedTextView = timeSelectedView.getChildAt(0) as TextView
//            val timeSelectedIndex =
//                profile.availableTimes[currDay]!!.indexOf(timeSelectedTextView.text.toString())
//            if (timeSelectedIndex > -1) {
//                profile.availableTimes[currDay]!!.remove(timeSelectedTextView.text.toString())
//                InternalStorage.writeObject(requireContext(), "profile", profile as Object)
//                timeSelectedTextView.background =
//                    getDrawable(requireContext(), R.drawable.unselected_rounded_time_option)
//                // change the day button to white if no time is selected for current day
//                if (profile.availableTimes[currDay]!!.size == 0) {
//                    selectedDays.remove(currDay)
//                    val currDayIndex = days.indexOf(currDay)
//                    val daySelectedView = day_selection.getChildAt(currDayIndex) as ConstraintLayout
//                    currDayTextView = daySelectedView.getChildAt(0) as TextView
//                    currDayTextView.background = getDrawable(
//                        requireContext(),
//                        R.drawable.unselected_scheduling_circle_button
//                    )
//                }
//                if (selectedDays.isEmpty()) callback!!.onSelectionEmpty()
//            } else {
//                profile.availableTimes[currDay]!!.add(timeSelectedTextView.text.toString())
//                InternalStorage.writeObject(requireContext(), "profile", profile as Object)
//                selectedDays.add(currDay)
//                timeSelectedTextView.background =
//                    getDrawable(requireContext(), R.drawable.selected_rounded_time_option)
//                // change day button to highlighted
//                val currDayIndex = days.indexOf(currDay)
//                val daySelectedView = day_selection.getChildAt(currDayIndex) as ConstraintLayout
//                currDayTextView = daySelectedView.getChildAt(0) as TextView
//                currDayTextView.background =
//                    getDrawable(requireContext(), R.drawable.selected_scheduling_circle_button)
//                // enable finish button
//                callback!!.onFilledOut()
//            }
//        }
    }

    private fun doubleTimeToString(time: Double): String {
        val minutes = if (floor(time) == time) "00" else "30"
        val hour = floor(time).toInt()
        val hours = if (hour > 12) (hour % 12).toString() else hour.toString()
        return "$hours:$minutes"
    }

    private fun stringTimeToDouble(time: String): Double {
        val (hours, minutes) = time.split(":").map { s -> Integer.parseInt(s) }
        var doubleTime = (if (hours < 9) hours + 12 else hours).toDouble()
        if (minutes != 0) {
            doubleTime += 0.5
        }
        return doubleTime
    }

    private var callback: OnFilledOutListener? = null

    fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    interface OnFilledOutListener {
        fun onFilledOut()
        fun onSelectionEmpty()
    }

    fun updateSchedule() {
        val availabilities = mutableListOf<Availability>()
        for ((day, times) in availableTimes) {
            if (times.isNotEmpty()) {
                availabilities.add(Availability(day, times.map { t -> stringTimeToDouble(t) }))
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            val updateAvailabilitiesEndpoint = Endpoint.updateAvailabilities(availabilities)
            val typeToken = object : TypeToken<ApiResponse<List<Availability>>>() {}.type
            val updateAvailabilitiesResponse = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<Availability>>>(
                    updateAvailabilitiesEndpoint.okHttpRequest(),
                    typeToken
                )
            }
            if (updateAvailabilitiesResponse == null || !updateAvailabilitiesResponse.success) {
                Toast.makeText(requireContext(), "Failed to save information", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}