package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.adapters.PlacesAdapter
import kotlinx.android.synthetic.main.fragment_scheduling_place.*


class SchedulingPlaceFragment : Fragment() {
    private val campusPlaces = arrayOf("Atrium Cafe", "Cafe Jennie", "Gimme Coffee", "Goldie’s Cafe",
        "Green Dragon", "Libe Cafe", "Mac’s Cafe", "Martha’s Cafe", "Mattin’s Cafe", "Temple of Zeus")
    private val collegetownPlaces = arrayOf("Kung Fu Tea", "Starbucks", "Mango Mango", "U Tea")

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_scheduling_place, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var campusAdapter =
            PlacesAdapter(
                context!!,
                campusPlaces
            )
        campusGridView.adapter = campusAdapter
        var collegetownAdapter =
            PlacesAdapter(
                context!!,
                collegetownPlaces
            )
        collegetownGridView.adapter = collegetownAdapter

        campusGridView.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            var campusSelectedIndex = campusAdapter.selectedPositions.indexOf(position)
            var campusSelectedPlace = campusGridView.getChildAt(position) as ConstraintLayout
            if (campusSelectedIndex > -1) {
                campusAdapter.selectedPositions.remove(position)
                campusSelectedPlace.background = getDrawable(context!!, R.drawable.location_scheduling_places_unselected)
                if (collegetownAdapter.selectedPositions.isEmpty() && campusAdapter.selectedPositions.isEmpty())
                    callback!!.onSelectionEmpty()
            } else {
                campusAdapter.selectedPositions.add(position)
                campusSelectedPlace.background = getDrawable(context!!, R.drawable.location_scheduling_places_selected)
                callback!!.onFilledOut()
            }
        }

        collegetownGridView.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            var ctownSelectedIndex = collegetownAdapter.selectedPositions.indexOf(position)
            var ctownSelectedPlace = collegetownGridView.getChildAt(position) as ConstraintLayout
            if (ctownSelectedIndex > -1) {
                collegetownAdapter.selectedPositions.remove(position)
                ctownSelectedPlace.background = getDrawable(context!!, R.drawable.location_scheduling_places_unselected)
                if (collegetownAdapter.selectedPositions.isEmpty() && campusAdapter.selectedPositions.isEmpty())
                    callback!!.onSelectionEmpty()
            } else {
                collegetownAdapter.selectedPositions.add(position)
                ctownSelectedPlace.background = getDrawable(context!!, R.drawable.location_scheduling_places_selected)
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