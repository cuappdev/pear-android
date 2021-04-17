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
import com.cornellappdev.coffee_chats_android.models.UserField.Category
import com.cornellappdev.coffee_chats_android.networking.*
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_interests_groups.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserFieldFragment : Fragment(), OnFilledOutObservable {

    private lateinit var category: Category

    /** pages that have a search bar */
    private val searchableContent = listOf(Category.GROUP, Category.TALKING_POINT)

    lateinit var adapter: UserFieldAdapter
    private lateinit var fieldAdapterArray: Array<UserField>

    /** master list of all valid options for this field */
    private lateinit var fieldTitles: Array<String>
    /** master list of all subtitles for this field */
    private lateinit var fieldSubtitles: Array<String>
    /** fields selected by the user */
    private lateinit var userFields: ArrayList<String>

    private var selectedColor = 0
    private var unselectedColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getSerializable(CATEGORY_TAG) as Category
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_interests_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // initialize lateinit vars
        fieldTitles = when (category) {
            Category.INTEREST, Category.TALKING_POINT -> resources.getStringArray(R.array.interest_titles)
            Category.GOAL -> resources.getStringArray(R.array.goal_titles)
            Category.GROUP -> emptyArray()
        }
        fieldSubtitles =
            if (category == Category.INTEREST || category == Category.TALKING_POINT)
                resources.getStringArray(R.array.interest_subtitles)
            else emptyArray()

        selectedColor = ContextCompat.getColor(requireContext(), R.color.onboardingListSelected)
        unselectedColor = ContextCompat.getColor(requireContext(), R.color.onboarding_fields)

        // signup_next is disabled until user has chosen at least one field
        callback!!.onSelectionEmpty()

        CoroutineScope(Dispatchers.Main).launch {
            // fetch fields already selected by user
            val userFieldEndpoint = when (category) {
                Category.INTEREST -> Endpoint.getUserInterests()
                Category.GROUP -> Endpoint.getUserGroups()
                Category.GOAL -> Endpoint.getUserGoals()
                Category.TALKING_POINT -> Endpoint.getUserTalkingPoints()
            }
            val typeToken = object : TypeToken<ApiResponse<List<String>>>() {}.type
            userFields = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<List<String>>>(
                    userFieldEndpoint.okHttpRequest(),
                    typeToken
                )
            }!!.data as ArrayList<String>
            // populate titles when backend requests are needed
            if (category in searchableContent) {
                val getGroupsEndpoint = Endpoint.getAllGroups()
                val groupTitles = withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<List<String>>>(
                        getGroupsEndpoint.okHttpRequest(),
                        typeToken
                    )!!.data.toTypedArray()
                }
                fieldTitles += groupTitles
            }
            // set up adapter
            fieldAdapterArray = Array(fieldTitles.size) { UserField() }
            for (i in fieldTitles.indices) {
                fieldAdapterArray[i] = UserField(
                    fieldTitles[i],
                    if (i < fieldSubtitles.size) fieldSubtitles[i] else ""
                )
            }
            fieldAdapterArray.sortBy { u -> u.getText() }
            adapter =
                UserFieldAdapter(
                    requireContext(),
                    fieldAdapterArray.toList(),
                    UserFieldAdapter.ItemColor.TOGGLE
                )
            interests_or_groups.adapter = adapter
            // display selected fields
            for (i in fieldAdapterArray.indices) {
                if (userFields.contains(fieldAdapterArray[i].getText())) {
                    fieldAdapterArray[i].setSelected()
                }
            }
            if (userFields.isNotEmpty()) {
                callback!!.onFilledOut()
            } else {
                callback!!.onSelectionEmpty()
            }
            setUpPage()
            interests_or_groups.setOnItemClickListener { _, view, position, _ ->
                val selectedView = view.findViewById<ConstraintLayout>(R.id.group_or_interest_box)
                val selectedText =
                    selectedView.findViewById<TextView>(R.id.group_or_interest_text).text
                val drawableBox = selectedView.background
                val currObj = fieldAdapterArray[position]
                currObj.toggleSelected()
                if (currObj.isSelected()) {
                    drawableBox.colorFilter =
                        BlendModeColorFilter(selectedColor, BlendMode.MULTIPLY)
                    userFields.add(currObj.getText())

                    callback!!.onFilledOut()
                } else {
                    drawableBox.colorFilter =
                        BlendModeColorFilter(unselectedColor, BlendMode.MULTIPLY)
                    userFields.remove(selectedText)
                    if (userFields.isEmpty()) {
                        callback!!.onSelectionEmpty()
                    }
                }
            }
        }
    }

    /**
     * Sets up or hides search bar depending on `content`
     */
    private fun setUpPage() {
        if (category in searchableContent) {
            group_search.visibility = View.VISIBLE
            group_search.queryHint = getString(R.string.groups_search_query_hint)

            val searchImgId =
                resources.getIdentifier("android:id/search_button", null, null)
            val searchIcon: ImageView =
                group_search.findViewById(searchImgId)
            searchIcon.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.searchHint), PorterDuff.Mode.DARKEN
            )
            // initialize searchview
            group_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    var outputArr = fieldAdapterArray
                    if (newText.isNotBlank()) {
                        val filtered = fieldAdapterArray.filter {
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
                    for (i in fieldAdapterArray.indices) {
                        if (userFields.contains(fieldAdapterArray[i].getText())) {
                            fieldAdapterArray[i].setSelected()
                        }
                    }
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
            })
        } else {
            group_search.visibility = View.GONE
        }
    }


    private var callback: OnFilledOutListener? = null

    override fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    override fun saveInformation() {
        val items = userFields
        if (category in searchableContent) {
            group_search.setQuery("", false)
        }
        updateUserField(requireContext(), items, category)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param category UserField category representing interests, groups, goals, or talking points
         * @return A new instance of fragment UserFieldFragment
         */
        @JvmStatic
        fun newInstance(category: Category) =
            UserFieldFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CATEGORY_TAG, category)
                }
            }

        private const val CATEGORY_TAG = "category"
    }
}