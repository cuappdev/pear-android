package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        send_feedback.setOnClickListener {
            visitWebsite("https://forms.gle/t9umWjckEs4NNWNS8/")
        }
        visit_website.setOnClickListener {
            visitWebsite("https://cornellappdev.com")
        }
        more_apps.setOnClickListener {
            visitWebsite("https://cornellappdev.com/apps")
        }
    }

    /** Starts an intent to visit the website at `url` */
    private fun visitWebsite(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}