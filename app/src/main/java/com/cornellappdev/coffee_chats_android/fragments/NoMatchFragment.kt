package com.cornellappdev.coffee_chats_android.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.R
import kotlinx.android.synthetic.main.fragment_no_match.*
import java.lang.reflect.Type


class NoMatchFragment : Fragment() {

    private var isPaused = false
    private var pauseExpiration = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isPaused = it.getBoolean(IS_PAUSED)
            pauseExpiration = it.getString(PAUSE_EXPIRATION)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_no_match, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val drawableResId =
            if (isPaused) R.drawable.ic_pair_of_pears else R.drawable.ic_surprised_pear
        no_match_image.setImageDrawable(ResourcesCompat.getDrawable(resources, drawableResId, null))

        val headerStrId =
            if (isPaused) R.string.no_match_pear_paused_header else R.string.no_match_header
        headerText.text = resources.getString(headerStrId)

        no_match_subheader.text = when {
            isPaused && pauseExpiration.isEmpty() -> resources.getString(R.string.pear_paused_indefinitely_subheader)
            isPaused && pauseExpiration.isNotEmpty() -> resources.getString(R.string.pear_paused_subheader, pauseExpiration)
            else -> resources.getString(R.string.no_match_subheader)
        }
        val typeface = if (isPaused) Typeface.NORMAL else Typeface.BOLD
        no_match_subheader.typeface = Typeface.create(no_match_subheader.typeface, typeface)
        primaryActionButton.visibility = if (isPaused) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance(isPaused: Boolean, pauseExpiration: String) =
            NoMatchFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_PAUSED, isPaused)
                    putString(PAUSE_EXPIRATION, pauseExpiration)
                }
            }

        private const val IS_PAUSED = "isPaused"
        private const val PAUSE_EXPIRATION = "pauseExpiration"
    }

}