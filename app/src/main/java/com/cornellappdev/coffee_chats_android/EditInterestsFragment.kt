package com.cornellappdev.coffee_chats_android

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import com.cornellappdev.coffee_chats_android.adapters.GroupInterestAdapter
import com.cornellappdev.coffee_chats_android.adapters.GroupInterestAdapter.ItemColor
import com.cornellappdev.coffee_chats_android.models.ApiResponse
import com.cornellappdev.coffee_chats_android.models.GroupOrInterest
import com.cornellappdev.coffee_chats_android.networking.*
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_edit_interests.*
import kotlinx.android.synthetic.main.interest_group_list_with_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val IS_INTEREST = "isInterest"

class EditInterestsFragment : Fragment() {
    private var isInterest = true
    private val selectedItems = ArrayList<GroupOrInterest>()
    private val moreItems = ArrayList<GroupOrInterest>()
    private lateinit var selectedItemsAdapter: GroupInterestAdapter
    private lateinit var moreItemsAdapter: GroupInterestAdapter

    private lateinit var interestTitles: Array<String>
    private lateinit var interestSubtitles: Array<String>
    private lateinit var groupTitles: Array<String>

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
        return inflater.inflate(R.layout.fragment_edit_interests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        interestTitles = resources.getStringArray(R.array.interest_titles)
        interestSubtitles = resources.getStringArray(R.array.interest_subtitles)

        selected_items.list_title.text = getString(R.string.your_interests)
        selected_items.list_subtitle.text = getString(R.string.deselect)
        more_items.list_title.text = getString(R.string.more_interests)
        more_items.list_subtitle.text = getString(R.string.tap_to_add)

        CoroutineScope(Dispatchers.Main).launch {
            if (isInterest) {
                val getUserInterestsEndpoint = Endpoint.getUserInterests()
                val interestTypeToken = object : TypeToken<ApiResponse<List<String>>>() {}.type
                val userInterests = withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<List<String>>>(
                        getUserInterestsEndpoint.okHttpRequest(),
                        interestTypeToken
                    )
                }!!.data as ArrayList<String>
                for ((i, interest) in interestTitles.withIndex()) {
                    val item = GroupOrInterest(
                        interestTitles[i],
                        if (i < interestSubtitles.size) interestSubtitles[i] else ""
                    )
                    if (interest in userInterests) {
                        selectedItems.add(item)
                    } else {
                        moreItems.add(item)
                    }
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
                val userGroups = withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<List<String>>>(
                        getUserGroupsEndpoint.okHttpRequest(),
                        groupTypeToken
                    )
                }!!.data as ArrayList<String>
                for ((i, group) in groupTitles.withIndex()) {
                    val item = GroupOrInterest(groupTitles[i])
                    if (group in userGroups) {
                        selectedItems.add(item)
                    } else {
                        moreItems.add(item)
                    }
                }
            }
            selectedItemsAdapter = GroupInterestAdapter(requireContext(), selectedItems, false, ItemColor.GREEN)
            selected_items.item_list.adapter = selectedItemsAdapter
            moreItemsAdapter = GroupInterestAdapter(requireContext(), moreItems, false, ItemColor.WHITE)
            more_items.item_list.adapter = moreItemsAdapter
            updatePage()
        }
    }

    private fun updatePage() {
        updateListViewHeight(selected_items.item_list, selectedItems.size)
        updateListViewHeight(more_items.item_list, moreItems.size)
    }

    /**
     * Updates the height of `listView` to show `listSize` items. Use this if the entire page needs
     * to scroll without the individually nested ListViews scrolling
     */
    private fun updateListViewHeight(listView: ListView, listSize: Int) {
        listView.layoutParams = (listView.layoutParams as LinearLayout.LayoutParams).apply {
            val displayMetrics = Resources.getSystem().displayMetrics
            val cellHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75f, displayMetrics).toInt()

            height = cellHeight * listSize
            listView.requestLayout()
            scrollView.requestLayout()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isInterest whether this fragment edits a group or interest
         * @return A new instance of fragment EditInterestsFragment
         */
        @JvmStatic
        fun newInstance(isInterest: Boolean) =
            EditInterestsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_INTEREST, isInterest)
                }
            }
    }
}