package com.cornellappdev.coffee_chats_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cornellappdev.coffee_chats_android.models.PearUser
import com.cornellappdev.coffee_chats_android.networking.getUser
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.pill_view.view.*
import kotlinx.android.synthetic.main.prompt_response_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
        CoroutineScope(Dispatchers.Main).launch {
            if (user == null) {
                user = getUser(userId!!)
            }
            setUpView(user!!)
        }
    }

    private fun setUpView(user: PearUser) {
        val c = requireContext()
        Glide.with(c).load(user.profilePicUrl).centerInside().circleCrop()
            .into(userImage)
        name.text = c.getString(R.string.user_name, user.firstName, user.lastName)
        reach_me.text = c.getString(R.string.reach_me, user.netId)
        val basicInfo = c.getString(
            R.string.basic_profile_info,
            user.majors.first().name, user.graduationYear, user.hometown, user.pronouns
        )
        basic_info.text = HtmlCompat.fromHtml(basicInfo, FROM_HTML_MODE_LEGACY)
        val ids = mutableListOf<Int>()
        user.interests.forEach {
            LayoutInflater.from(c).inflate(
                R.layout.pill_view,
                interests_pill_list,
                false
            ).apply {
                id = View.generateViewId()
                ids.add(id)
                text_view.text = it.name
                Glide.with(c).load(it.imageUrl).into(icon)
                interests_pill_list.addView(this)
            }
        }
        interests_pill_flow.referencedIds = ids.toIntArray()

        ids.clear()
        user.groups.forEach {
            LayoutInflater.from(c).inflate(
                R.layout.pill_view,
                groups_pill_list,
                false
            ).apply {
                id = View.generateViewId()
                ids.add(id)
                text_view.text = it.name
                Glide.with(c).load(it.imageUrl).into(icon)
                groups_pill_list.addView(this)
            }
        }
        groups_pill_flow.referencedIds = ids.toIntArray()

        user.prompts.forEach {
            LayoutInflater.from(c)
                .inflate(R.layout.prompt_response_view, prompt_responses_list, false).apply {
                id = View.generateViewId()
                prompt.text = it.name
                response.text = it.answer
                prompt_responses_list.addView(this)
            }
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