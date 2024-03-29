package com.cornellappdev.coffee_chats_android.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cornellappdev.coffee_chats_android.ProfileSettingsActivity
import com.cornellappdev.coffee_chats_android.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settings_nav_view.itemIconTintList = null
        settings_nav_view.setNavigationItemSelectedListener((activity as ProfileSettingsActivity).settingsNavigationListener)
    }
}