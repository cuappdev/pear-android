package com.cornellappdev.coffee_chats_android.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.OnFilledOutListener
import com.cornellappdev.coffee_chats_android.OnPauseChangedListener
import com.cornellappdev.coffee_chats_android.OnPauseChangedObservable
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.networking.updatePauseStatus
import kotlinx.android.synthetic.main.fragment_no_match.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class NoMatchFragment : Fragment(), OnPauseChangedObservable {

    private var isPaused = false
    private var pauseExpiration = ""

    data class NoMatchUIConfig(
        val drawableResId: Int,
        val headerStrResId: Int,
        val subheaderTypeface: Int,
        val actionButtonVisibility: Int
    )

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
        val uiConfig = if (isPaused) pausedUIConfig else noMatchUIConfig
        no_match_image.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                uiConfig.drawableResId,
                null
            )
        )
        headerText.text = resources.getString(uiConfig.headerStrResId)
        no_match_subheader.typeface =
            Typeface.create(no_match_subheader.typeface, uiConfig.subheaderTypeface)
        primaryActionButton.visibility = uiConfig.actionButtonVisibility

        no_match_subheader.text = when {
            isPaused && pauseExpiration.isEmpty() -> resources.getString(R.string.pear_paused_indefinitely_subheader)
            isPaused && pauseExpiration.isNotEmpty() -> {
                val pauseDate = pauseExpiration.substringBefore("T")
                val inFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val date = inFormatter.parse(pauseDate)
                val outFormatter = SimpleDateFormat("MMMM dd", Locale.ENGLISH)
                val formattedPauseExpiration = outFormatter.format(date!!)
                resources.getString(R.string.pear_paused_subheader, formattedPauseExpiration)
            }
            else -> resources.getString(R.string.no_match_subheader)
        }

        primaryActionButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                updatePauseStatus(false)
                callback?.onPauseChanged(false)
            }
        }
    }

    private var callback: OnPauseChangedListener? = null

    override fun setOnPauseChangedListener(callback: OnPauseChangedListener) {
        this.callback = callback
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

        private val pausedUIConfig = NoMatchUIConfig(
            drawableResId = R.drawable.ic_pair_of_pears,
            headerStrResId = R.string.no_match_pear_paused_header,
            subheaderTypeface = Typeface.NORMAL,
            actionButtonVisibility = View.VISIBLE
        )
        private val noMatchUIConfig = NoMatchUIConfig(
            drawableResId = R.drawable.surprised_pear,
            headerStrResId = R.string.no_match_header,
            subheaderTypeface = Typeface.BOLD,
            actionButtonVisibility = View.INVISIBLE
        )
    }

}