package com.cornellappdev.coffee_chats_android

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_scheduling_place.*


class SchedulingPlaceActivity : AppCompatActivity() {
    private val campusPlaces = arrayOf("Atrium Cafe", "Cafe Jennie", "Gimme Coffee", "Goldie’s Cafe",
    "Green Dragon", "Libe Cafe", "Mac’s Cafe", "Martha’s Cafe", "Mattin’s Cafe", "Temple of Zeus")
    private val collegetownPlaces = arrayOf("Kung Fu Tea", "Starbucks", "Mango Mango", "U Tea")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduling_place)
        var campusAdapter = PlacesAdapter(this, campusPlaces)
        campusGridView.adapter = campusAdapter
        var collegetownAdapter = PlacesAdapter(this, collegetownPlaces)
        collegetownGridView.adapter = collegetownAdapter
        finishButton.isEnabled = false

        campusGridView.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            var campusSelectedIndex = campusAdapter.selectedPositions.indexOf(position)
            var campusSelectedPlace = campusGridView.getChildAt(position) as ConstraintLayout
            if (campusSelectedIndex > -1) {
                campusAdapter.selectedPositions.remove(position)
                campusSelectedPlace.background = getDrawable(R.drawable.location_scheduling_places_unselected)
                if (collegetownAdapter.selectedPositions.isEmpty() && campusAdapter.selectedPositions.isEmpty())
                    finishButton.isEnabled = false
            } else {
                campusAdapter.selectedPositions.add(position)
                campusSelectedPlace.background = getDrawable(R.drawable.location_scheduling_places_selected)
                finishButton.isEnabled = true
            }
        }

        collegetownGridView.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            var ctownSelectedIndex = collegetownAdapter.selectedPositions.indexOf(position)
            var ctownSelectedPlace = collegetownGridView.getChildAt(position) as ConstraintLayout
            if (ctownSelectedIndex > -1) {
                collegetownAdapter.selectedPositions.remove(position)
                ctownSelectedPlace.background = getDrawable(R.drawable.location_scheduling_places_unselected)
                if (collegetownAdapter.selectedPositions.isEmpty() && campusAdapter.selectedPositions.isEmpty())
                    finishButton.isEnabled = false
            } else {
                collegetownAdapter.selectedPositions.add(position)
                ctownSelectedPlace.background = getDrawable(R.drawable.location_scheduling_places_selected)
                finishButton.isEnabled = true
            }
        }
    }
}