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


class SchedulingPlaceFragment : Fragment() {
    //    private val campusPlaces = arrayOf("Atrium Cafe", "Cafe Jennie", "Gimme Coffee", "Goldie’s Cafe",
//        "Green Dragon", "Libe Cafe", "Mac’s Cafe", "Martha’s Cafe", "Mattin’s Cafe", "Temple of Zeus")
    private lateinit var campusPlaces: Array<String>
    private lateinit var collegetownPlaces: Array<String>
    private val preferredLocations = mutableListOf<String>()
//    private val collegetownPlaces = arrayOf("Kung Fu Tea", "Starbucks", "Mango Mango", "U Tea")

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
            val getUserLocationsEndpoint = Endpoint.getUserLocations()
            val typeToken = object : TypeToken<ApiResponse<List<Location>>>() {}.type
            val locations = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<Location>>>(
                    getUserLocationsEndpoint.okHttpRequest(),
                    typeToken
                )
            }!!.data
            for ((_, place) in locations) {
                preferredLocations.add(place)
            }
            if (preferredLocations.isNotEmpty()) {
                callback!!.onFilledOut()
            }
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
                        if (preferredLocations.isEmpty()) callback!!.onSelectionEmpty()
                    } else {
                        preferredLocations.add(campusPlaces[position])
                        campusSelectedPlace.background =
                            getDrawable(
                                requireContext(),
                                R.drawable.location_scheduling_places_selected
                            )
                        callback!!.onFilledOut()
                    }
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
                        if (preferredLocations.isEmpty()) callback!!.onSelectionEmpty()
                    } else {
                        preferredLocations.add(collegetownPlaces[position])
                        ctownSelectedPlace.background = getDrawable(
                            requireContext(),
                            R.drawable.location_scheduling_places_selected
                        )
                        callback!!.onFilledOut()
                    }
                }
        }
//        val profile = InternalStorage.readObject(context!!, "profile") as UserProfile
//        val campusAdapter =
//            PlacesAdapter(
//                context!!,
//                campusPlaces,
//                profile.preferredLocations
//            )
//        campusGridView.adapter = campusAdapter
//        val collegetownAdapter =
//            PlacesAdapter(
//                context!!,
//                collegetownPlaces,
//                profile.preferredLocations
//            )
//        collegetownGridView.adapter = collegetownAdapter
//
//        campusGridView.onItemClickListener = OnItemClickListener { parent, v, position, id ->
//            val campusSelectedPlace = campusGridView.getChildAt(position) as ConstraintLayout
//            if (profile.preferredLocations.contains(campusPlaces[position])) {
//                profile.preferredLocations.remove(campusPlaces[position])
//                InternalStorage.writeObject(context!!, "profile", profile as Object)
//                campusSelectedPlace.background = getDrawable(context!!, R.drawable.location_scheduling_places_unselected)
//                if (profile.preferredLocations.isEmpty()) callback!!.onSelectionEmpty()
//            } else {
//                profile.preferredLocations.add(campusPlaces[position])
//                InternalStorage.writeObject(context!!, "profile", profile as Object)
//                campusSelectedPlace.background = getDrawable(context!!, R.drawable.location_scheduling_places_selected)
//                callback!!.onFilledOut()
//            }
//        }
//
//        collegetownGridView.onItemClickListener = OnItemClickListener { parent, v, position, id ->
//            val ctownSelectedPlace = collegetownGridView.getChildAt(position) as ConstraintLayout
//            if (profile.preferredLocations.contains(collegetownPlaces[position])) {
//                profile.preferredLocations.remove(collegetownPlaces[position])
//                InternalStorage.writeObject(context!!, "profile", profile as Object)
//                ctownSelectedPlace.background = getDrawable(context!!, R.drawable.location_scheduling_places_unselected)
//                if (profile.preferredLocations.isEmpty()) callback!!.onSelectionEmpty()
//            } else {
//                profile.preferredLocations.add(collegetownPlaces[position])
//                InternalStorage.writeObject(context!!, "profile", profile as Object)
//                ctownSelectedPlace.background = getDrawable(context!!, R.drawable.location_scheduling_places_selected)
//                callback!!.onFilledOut()
//            }
//        }
    }

    private var callback: OnFilledOutListener? = null

    fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    interface OnFilledOutListener {
        fun onFilledOut()
        fun onSelectionEmpty()
    }

    fun updateLocations() {
        val locations = mutableListOf<Location>()
        for (loc in preferredLocations) {
            val area = if (campusPlaces.contains(loc)) "Campus" else "Collegetown"
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