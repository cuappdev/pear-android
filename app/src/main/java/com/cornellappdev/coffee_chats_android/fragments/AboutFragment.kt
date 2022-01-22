package com.cornellappdev.coffee_chats_android.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cornellappdev.coffee_chats_android.R
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
            visitWebsite(FEEDBACK_URL)
        }
        visit_website.setOnClickListener {
            visitWebsite(WEBSITE_URL)
        }
        more_apps.setOnClickListener {
            visitWebsite(MORE_APPS_URL)
        }
    }

    /** Starts an intent to visit the website at `url` */
    private fun visitWebsite(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    companion object {
        private const val FEEDBACK_URL = "https://forms.gle/t9umWjckEs4NNWNS8/"
        private const val WEBSITE_URL = "https://cornellappdev.com"
        private const val MORE_APPS_URL = "https://cornellappdev.com/apps"
    }
}