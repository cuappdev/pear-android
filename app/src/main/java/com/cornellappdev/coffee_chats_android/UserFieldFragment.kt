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
import com.cornellappdev.coffee_chats_android.models.UserField
import com.cornellappdev.coffee_chats_android.models.UserField.Category
import com.cornellappdev.coffee_chats_android.networking.getAllGroups
import com.cornellappdev.coffee_chats_android.networking.getAllInterests
import com.cornellappdev.coffee_chats_android.networking.getAllPurposes
import com.cornellappdev.coffee_chats_android.networking.getUser
import kotlinx.android.synthetic.main.fragment_interests_groups.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserFieldFragment : Fragment(), OnFilledOutObservable {

    private lateinit var category: Category

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
                    getAllGroups().map { UserField(text = it.name, drawableUrl = it.imageUrl, id = it.id) }
                }
            }.toTypedArray()

            selectedColor = ContextCompat.getColor(requireContext(), R.color.onboardingListSelected)
            unselectedColor = ContextCompat.getColor(requireContext(), R.color.onboarding_fields)

            // signup_next is disabled until user has chosen at least one field
            callback!!.onSelectionEmpty()

            val user = getUser()
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
            adapter =
                UserFieldAdapter(
                    requireContext(),
                    fieldAdapterArray.toList(),
                    UserFieldAdapter.ItemColor.TOGGLE,
                    true // TODO- display icons
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
            userFields.map { userField -> fieldAdapterArray.first { it.getText() == userField }.id }
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