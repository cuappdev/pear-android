package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.models.PearUser
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.pill_view.view.*


class ProfileFragment : Fragment() {
    private var userId: Int? = null
    private var user: PearUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable(USER)
            if (user == null && it.containsKey(USER_ID)) {
                userId = it.getInt(USER_ID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (user == null) {
            // make network call to get user profile
        }

        val interests = listOf("Reading", "Coding", "Crosswords", "Jigsaw", "Jenga")
        (interests.indices).forEach { i ->
            val pillView = LayoutInflater.from(requireContext()).inflate(
                R.layout.pill_view,
                interests_pill_list,
                false
            ).apply {
                id = View.generateViewId()
            }
            pillView.basic_info.text = interests[i]
            interests_pill_list.addView(pillView)
            interests_pill_flow.addView(pillView)
        }
        val groups = listOf("Flat Earth Society", "Cornell AppDev")
        (groups.indices).forEach { i ->
            val pillView = LayoutInflater.from(requireContext()).inflate(
                R.layout.pill_view,
                groups_pill_list,
                false
            ).apply {
                id = View.generateViewId()
            }
            pillView.basic_info.text = groups[i]
            groups_pill_list.addView(pillView)
            groups_pill_flow.addView(pillView)
        }
    }

    companion object {
        /**
         * @param userId Id of current user
         * @return A new instance of fragment ProfileFragment
         */
        @JvmStatic
        fun newInstance(userId: Int) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                }
            }

        @JvmStatic
        fun newInstance(pearUser: PearUser) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(USER, pearUser)
                }
            }

        private const val USER_ID = "userId"
        private const val USER = "user"
    }
}