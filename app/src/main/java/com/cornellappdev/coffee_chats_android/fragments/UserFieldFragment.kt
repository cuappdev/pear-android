package com.cornellappdev.coffee_chats_android.fragments

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
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
import com.cornellappdev.coffee_chats_android.OnFilledOutListener
import com.cornellappdev.coffee_chats_android.OnFilledOutObservable
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.adapters.UserFieldAdapter
import com.cornellappdev.coffee_chats_android.models.Group
import com.cornellappdev.coffee_chats_android.models.Interest
import com.cornellappdev.coffee_chats_android.models.UserField
import com.cornellappdev.coffee_chats_android.models.UserField.Category
import com.cornellappdev.coffee_chats_android.networking.getAllGroups
import com.cornellappdev.coffee_chats_android.networking.getAllInterests
import com.cornellappdev.coffee_chats_android.networking.getAllPurposes
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.cornellappdev.coffee_chats_android.singletons.UserSingleton
import com.cornellappdev.coffee_chats_android.updateUserField
import kotlinx.android.synthetic.main.fragment_interests_groups.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserFieldFragment : Fragment(), OnFilledOutObservable {

    private lateinit var category: Category

    /** whether to fetch user info from `UserRepository` instead of backend */
    private var useRepository: Boolean = false

    /** whether to hide fields that the user has already selected */
    private var hideSelectedFields: Boolean = false

    /** pages that have a search bar - kept as a list to facilitate adding searchable fragments */
    private val searchableContent = listOf(Category.GROUP)

    lateinit var adapter: UserFieldAdapter
    private lateinit var fieldAdapterArray: Array<UserField>
    private lateinit var currFieldAdapterArray: Array<UserField>

    /** fields selected by the user */
    private lateinit var userFields: ArrayList<String>

    private var selectedColor = 0
    private var unselectedColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getSerializable(CATEGORY_TAG) as Category
            useRepository = it.getBoolean(USE_REPOSITORY_TAG)
            hideSelectedFields = it.getBoolean(HIDE_SELECTED_FIELDS_TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_interests_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        CoroutineScope(Dispatchers.Main).launch {
            // initialize lateinit vars
            fieldAdapterArray = when (category) {
                Category.INTEREST -> {
                    getAllInterests().map {
                        UserField(
                            text = it.name,
                            subtext = it.subtitle,
                            drawableUrl = it.imageUrl,
                            id = it.id
                        )
                    }
                }
                Category.GOAL -> {
                    getAllPurposes().map { UserField(text = it.name, id = it.id) }
                }
                Category.GROUP -> {
                    getAllGroups().map {
                        UserField(
                            text = it.name,
                            drawableUrl = it.imageUrl,
                            id = it.id
                        )
                    }
                }
            }.toTypedArray()

            selectedColor = ContextCompat.getColor(requireContext(), R.color.onboardingListSelected)
            unselectedColor = ContextCompat.getColor(requireContext(), R.color.onboarding_fields)

            // signup_next is disabled until user has chosen at least one field
            callback!!.onSelectionEmpty()

            val user = if (useRepository) UserSingleton.user else getUser()
            // fetch fields already selected by user
            // TODO- refactor to only store IDs?
            userFields = ArrayList(
                when (category) {
                    Category.INTEREST -> user.interests.map { it.name }
                    Category.GROUP -> user.groups.map { it.name }
                    Category.GOAL -> user.purposes.map { it.name }
                }
            )
            // set up adapter
            fieldAdapterArray.sortBy { u -> u.getText() }
            if (hideSelectedFields) {
                fieldAdapterArray =
                    fieldAdapterArray.filter { !userFields.contains(it.getText()) }.toTypedArray()
            }
            adapter =
                UserFieldAdapter(
                    requireContext(),
                    fieldAdapterArray.toList(),
                    UserFieldAdapter.ItemColor.TOGGLE,
                    category == Category.GOAL
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
                val currObj = currFieldAdapterArray[position]
                currObj.toggleSelected()
                if (currObj.isSelected()) {
                    drawableBox.colorFilter =
                        PorterDuffColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY)
                    userFields.add(currObj.getText())

                    callback!!.onFilledOut()
                } else {
                    drawableBox.colorFilter =
                        PorterDuffColorFilter(unselectedColor, PorterDuff.Mode.MULTIPLY)
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
        currFieldAdapterArray = fieldAdapterArray
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
                    currFieldAdapterArray = fieldAdapterArray
                    if (newText.isNotBlank()) {
                        val filtered = fieldAdapterArray.filter {
                            it.getText().toLowerCase().contains(newText.toLowerCase())
                        }.toTypedArray()
                        currFieldAdapterArray = filtered
                    }
                    adapter =
                        UserFieldAdapter(
                            requireContext(),
                            currFieldAdapterArray.toList(),
                            UserFieldAdapter.ItemColor.TOGGLE,
                            category == Category.GOAL
                        )
                    interests_or_groups.adapter = adapter
                    for (i in currFieldAdapterArray.indices) {
                        if (userFields.contains(currFieldAdapterArray[i].getText())) {
                            currFieldAdapterArray[i].setSelected()
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
        val items =
            userFields.mapNotNull { userField -> fieldAdapterArray.firstOrNull { it.getText() == userField } }
        if (category in searchableContent) {
            group_search.setQuery("", false)
        }
        // save to repository
        if (useRepository) {
            if (category == Category.INTEREST) {
                val interests =
                    items.map { Interest(it.id, it.getText(), it.getSubtext(), it.drawableUrl) }
                for (interest in interests) {
                    UserSingleton.addInterest(interest)
                }
            }
            if (category == Category.GROUP) {
                val groups = items.map { Group(it.id, it.getText(), it.drawableUrl) }
                for (group in groups) {
                    UserSingleton.addGroup(group)
                }
            }
        } else {
            val indices = items.map { it.id }
            updateUserField(requireContext(), indices, category)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param category UserField category representing interests, groups, goals, or talking points
         * @param useRepository Load user data from `UserRepository`
         * @param hideSelectedFields Hides fields already selected by user
         * @return A new instance of fragment UserFieldFragment
         */
        @JvmStatic
        fun newInstance(
            category: Category,
            useRepository: Boolean = false,
            hideSelectedFields: Boolean = false
        ) =
            UserFieldFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CATEGORY_TAG, category)
                    putSerializable(USE_REPOSITORY_TAG, useRepository)
                    putSerializable(HIDE_SELECTED_FIELDS_TAG, hideSelectedFields)
                }
            }

        private const val CATEGORY_TAG = "category"
        private const val USE_REPOSITORY_TAG = "useRepository"
        private const val HIDE_SELECTED_FIELDS_TAG = "hideAddedFields"
    }
}