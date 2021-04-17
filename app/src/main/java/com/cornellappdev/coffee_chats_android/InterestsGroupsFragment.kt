package com.cornellappdev.coffee_chats_android

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.adapters.UserFieldAdapter
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.UserField
import com.cornellappdev.coffee_chats_android.networking.*
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_interests_groups.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InterestsGroupsFragment : Fragment(), OnFilledOutObservable {

    private var isInterest = true

    lateinit var adapter: UserFieldAdapter
    private lateinit var interestTitles: Array<String>
    private lateinit var interestSubtitles: Array<String>
    private lateinit var groupTitles: Array<String>

    private lateinit var userInterests: ArrayList<String>
    private lateinit var userGroups: ArrayList<String>

    private var selectedColor = 0
    private var unselectedColor = 0

    private lateinit var interests: Array<UserField>
    private lateinit var groups: Array<UserField>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isInterest = it.getBoolean(IS_INTEREST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_interests_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        interestTitles = resources.getStringArray(R.array.interest_titles)
        interestSubtitles = resources.getStringArray(R.array.interest_subtitles)

        selectedColor = ContextCompat.getColor(requireContext(), R.color.onboardingListSelected)
        unselectedColor = ContextCompat.getColor(requireContext(), R.color.onboarding_fields)

        // signup_next is disabled until user has chosen at least one interest
        callback!!.onSelectionEmpty()

        CoroutineScope(Dispatchers.Main).launch {
            if (isInterest) {
                val getUserInterestsEndpoint = Endpoint.getUserInterests()
                val interestTypeToken = object : TypeToken<ApiResponse<List<String>>>() {}.type
                userInterests = withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<List<String>>>(
                        getUserInterestsEndpoint.okHttpRequest(),
                        interestTypeToken
                    )
                }!!.data as ArrayList<String>
                interests = Array(interestTitles.size) {
                    UserField()
                }
                for (i in interestTitles.indices) {
                    interests[i] = UserField(
                        interestTitles[i],
                        if (i < interestSubtitles.size) interestSubtitles[i] else ""
                    )
                }
            } else {
                val getGroupsEndpoint = Endpoint.getAllGroups()
                val groupTypeToken = object : TypeToken<ApiResponse<List<String>>>() {}.type
                groupTitles = withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<List<String>>>(
                        getGroupsEndpoint.okHttpRequest(),
                        groupTypeToken
                    )!!.data.toTypedArray()
                }
                val getUserGroupsEndpoint = Endpoint.getUserGroups()
                userGroups = withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<List<String>>>(
                        getUserGroupsEndpoint.okHttpRequest(),
                        groupTypeToken
                    )
                }!!.data as ArrayList<String>
                groups = Array(groupTitles.size) {
                    UserField()
                }
                for (i in groupTitles.indices) {
                    groups[i] = UserField(groupTitles[i])
                }
            }
            updatePage()
            interests_or_groups.setOnItemClickListener { _, view, position, _ ->
                val selectedView = view.findViewById<ConstraintLayout>(R.id.group_or_interest_box)
                val selectedText =
                    selectedView.findViewById<TextView>(R.id.group_or_interest_text).text
                val drawableBox = selectedView.background
                val currObj =
                    if (isInterest) interests[position]
                    else groups[groupTitles.indexOf(selectedText)]
                currObj.toggleSelected()
                if (currObj.isSelected()) {
                    drawableBox.colorFilter =
                        BlendModeColorFilter(selectedColor, BlendMode.MULTIPLY)
                    if (isInterest) userInterests.add(currObj.getText())
                    else userGroups.add(currObj.getText())

                    callback!!.onFilledOut()
                } else {
                    drawableBox.colorFilter =
                        BlendModeColorFilter(unselectedColor, BlendMode.MULTIPLY)
                    if (isInterest) {
                        userInterests.remove(selectedText)
                        if (userInterests.isEmpty()) {
                            callback!!.onSelectionEmpty()
                        }
                    } else {
                        userGroups.remove(selectedText)
                        if (userGroups.isEmpty()) {
                            callback!!.onSelectionEmpty()
                        }
                    }
                }
            }
        }
    }

    private fun updatePage() {
        if (isInterest) {
            group_search.visibility = View.GONE
            adapter =
                UserFieldAdapter(
                    requireContext(),
                    interests.toList(),
                    UserFieldAdapter.ItemColor.TOGGLE
                )
            interests_or_groups.adapter = adapter

            for (i in interestTitles.indices) {
                if (userInterests.contains(interestTitles[i])) {
                    interests[i].setSelected()
                }
            }

            if (userInterests.isNotEmpty()) {
                callback!!.onFilledOut()
            } else {
                callback!!.onSelectionEmpty()
            }
        } else {
            group_search.visibility = View.VISIBLE
            group_search.queryHint = getString(R.string.groups_search_query_hint)

            val searchImgId =
                resources.getIdentifier("android:id/search_button", null, null)
            val searchIcon: ImageView =
                group_search.findViewById(searchImgId)
            searchIcon.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.searchHint), PorterDuff.Mode.DARKEN
            )
            adapter =
                UserFieldAdapter(
                    requireContext(), groups.toList(), UserFieldAdapter.ItemColor.TOGGLE
                )

            interests_or_groups.adapter = adapter
            // initialize searchview
            group_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    var outputArr = groups
                    if (newText.isNotBlank()) {
                        val filtered = groups.filter {
                            it.getText().toLowerCase().contains(newText.toLowerCase())
                        }.toTypedArray()
                        outputArr = filtered
                    }
                    adapter =
                        UserFieldAdapter(
                            requireContext(),
                            outputArr.toList(),
                            UserFieldAdapter.ItemColor.TOGGLE
                        )
                    interests_or_groups.adapter = adapter
                    for (i in groupTitles.indices) {
                        if (userGroups.contains(groupTitles[i])) {
                            groups[i].setSelected()
                        }
                    }
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
            })
            for (i in groupTitles.indices) {
                if (userGroups.contains(groupTitles[i])) {
                    groups[i].setSelected()
                }
            }

            if (userGroups.isNotEmpty()) {
                callback!!.onFilledOut()
            } else {
                callback!!.onSelectionEmpty()
            }
        }
    }


    private var callback: OnFilledOutListener? = null

    override fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    override fun saveInformation() {
        val items = if (isInterest) userInterests else userGroups
        if (!isInterest) {
            group_search.setQuery("", false)
        }
        updateUserField(requireContext(), items, if (isInterest) UserField.Category.INTEREST else UserField.Category.GROUP)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isInterest whether this fragment allows users to input interests or groups
         * @return A new instance of fragment InterestsGroupsFragment
         */
        @JvmStatic
        fun newInstance(isInterest: Boolean) =
            InterestsGroupsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_INTEREST, isInterest)
                }
            }

        private const val IS_INTEREST = "isInterest"
    }
}