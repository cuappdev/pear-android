package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.adapters.PlacesAdapter
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.Location
import com.cornellappdev.coffee_chats_android.networking.*
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_scheduling_place.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SchedulingPlaceFragment : Fragment(), OnFilledOutObservable {
    private lateinit var campusPlaces: Array<String>
    private lateinit var collegetownPlaces: Array<String>
    private val preferredLocations = mutableListOf<String>()

    // currently requiring a minimum of 3 places
    private val minRequiredLocations = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_scheduling_place, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        campusPlaces = resources.getStringArray(R.array.campus_locations)
        collegetownPlaces = resources.getStringArray(R.array.collegetown_locations)
        CoroutineScope(Dispatchers.Main).launch {
            // get existing selected locations
            val getUserLocationsEndpoint = Endpoint.getUserLocations()
            val typeToken = object : TypeToken<ApiResponse<List<Location>>>() {}.type
            val locations = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<Location>>>(
                    getUserLocationsEndpoint.okHttpRequest(),
                    typeToken
                )
            }!!.data
            for ((_, place) in locations!!) {
                preferredLocations.add(place)
            }
            toggleNextButton()

            // set up adapters
            val campusAdapter = PlacesAdapter(requireContext(), campusPlaces, preferredLocations)
            campusGridView.adapter = campusAdapter
            val collegetownAdapter =
                PlacesAdapter(
                    requireContext(),
                    collegetownPlaces,
                    preferredLocations
                )
            collegetownGridView.adapter = collegetownAdapter

            campusGridView.onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    val campusSelectedPlace =
                        campusGridView.getChildAt(position) as ConstraintLayout
                    if (preferredLocations.contains(campusPlaces[position])) {
                        preferredLocations.remove(campusPlaces[position])
                        campusSelectedPlace.background =
                            getDrawable(
                                requireContext(),
                                R.drawable.location_scheduling_places_unselected
                            )
                    } else {
                        preferredLocations.add(campusPlaces[position])
                        campusSelectedPlace.background =
                            getDrawable(
                                requireContext(),
                                R.drawable.location_scheduling_places_selected
                            )
                    }
                    toggleNextButton()
                }
            collegetownGridView.onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    val ctownSelectedPlace =
                        collegetownGridView.getChildAt(position) as ConstraintLayout
                    if (preferredLocations.contains(collegetownPlaces[position])) {
                        preferredLocations.remove(collegetownPlaces[position])
                        ctownSelectedPlace.background = getDrawable(
                            requireContext(),
                            R.drawable.location_scheduling_places_unselected
                        )
                    } else {
                        preferredLocations.add(collegetownPlaces[position])
                        ctownSelectedPlace.background = getDrawable(
                            requireContext(),
                            R.drawable.location_scheduling_places_selected
                        )
                    }
                    toggleNextButton()
                }
        }
    }

    // enables or disables the next button based on the number of selected locations
    private fun toggleNextButton() {
        if (preferredLocations.size >= minRequiredLocations) {
            callback!!.onFilledOut()
        } else {
            callback!!.onSelectionEmpty()
        }
    }

    private var callback: OnFilledOutListener? = null

    override fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    override fun saveInformation() {
        val locations = mutableListOf<Location>()
        val campusArea = getString(R.string.campus)
        val collegetownArea = getString(R.string.collegetown)
        for (loc in preferredLocations) {
            val area = if (campusPlaces.contains(loc)) campusArea else collegetownArea
            locations.add(Location(area, loc))
        }
        CoroutineScope(Dispatchers.Main).launch {
            val updateLocationsEndpoint = Endpoint.updateLocations(locations)
            val typeToken = object : TypeToken<ApiResponse<List<Location>>>() {}.type
            val updateLocationResponse = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<Location>>>(
                    updateLocationsEndpoint.okHttpRequest(),
                    typeToken
                )
            }
            if (updateLocationResponse == null || !updateLocationResponse.success) {
                Toast.makeText(requireContext(), "Failed to save information", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}