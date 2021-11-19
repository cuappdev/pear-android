package com.cornellappdev.coffee_chats_android

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.adapters.UserFieldAdapter
import com.cornellappdev.coffee_chats_android.adapters.UserFieldAdapter.ItemColor
import com.cornellappdev.coffee_chats_android.models.UserField
import com.cornellappdev.coffee_chats_android.models.UserField.Category
import com.cornellappdev.coffee_chats_android.networking.getAllGroups
import com.cornellappdev.coffee_chats_android.networking.getAllInterests
import com.cornellappdev.coffee_chats_android.networking.getUser
import kotlinx.android.synthetic.main.fragment_edit_interests.*
import kotlinx.android.synthetic.main.fragment_edit_interests.view.*
import kotlinx.android.synthetic.main.interest_group_list_with_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class EditInterestsGroupsFragment : Fragment(), OnFilledOutObservable {
    private var isInterest = true
    private lateinit var itemString: String
    private val selectedItems = ArrayList<UserField>()
    private val moreItems = ArrayList<UserField>()
    private lateinit var selectedItemsAdapter: UserFieldAdapter
    private lateinit var moreItemsAdapter: UserFieldAdapter

    /** whether to display all selected items, or just the first 3 */
    private var showExcessSelectedItems = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isInterest = it.getBoolean(IS_INTEREST)
            itemString = if (isInterest) "interests" else "groups"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_interests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selected_items.list_title.text =
            getString(if (isInterest) R.string.menu_interests else R.string.menu_groups)
        more_items.list_title.text = getString(R.string.more_items, itemString)
        more_items.list_subtitle.text = getString(R.string.tap_to_add)

        CoroutineScope(Dispatchers.Main).launch {
            val user = getUser()
            if (isInterest) {
                val userInterests = user.interests
                val allInterests = getAllInterests()
                for (interest in allInterests) {
                    val item = UserField( interest.name, interest.subtitle, interest.imageUrl, id = interest.id)
                    if (interest in userInterests) {
                        selectedItems.add(item)
                    } else {
                        moreItems.add(item)
                    }
                }
            } else {
                val userGroups = user.groups
                val allGroups = getAllGroups()
                for (group in allGroups) {
                    val item = UserField(group.name, drawableUrl = group.imageUrl, id = group.id)
                    if (group in userGroups) {
                        selectedItems.add(item)
                    } else {
                        moreItems.add(item)
                    }
                }
            }

            selectedItemsAdapter =
                UserFieldAdapter(requireContext(), selectedItems, ItemColor.GREEN, false)
            selected_items.item_list.adapter = selectedItemsAdapter
            moreItemsAdapter =
                UserFieldAdapter(requireContext(), moreItems, ItemColor.WHITE, false)
            more_items.item_list.adapter = moreItemsAdapter
            view_other_items.setOnClickListener {
                showExcessSelectedItems = !showExcessSelectedItems
                updatePage()
            }
            selected_items.item_list.setOnItemClickListener { _, _, position, _ ->
                moveItem(position, selectedItems, moreItems)
            }
            more_items.item_list.setOnItemClickListener { _, _, position, _ ->
                moveItem(position, moreItems, selectedItems)
            }
            toggleSaveButton()
            updatePage()
        }
    }

    private fun updatePage() {
        if (selectedItems.size > 3) {
            view_other_items.visibility = View.VISIBLE
            view_other_items.arrow.rotation = if (showExcessSelectedItems) 90f else 270f
        } else {
            view_other_items.visibility = View.GONE
            showExcessSelectedItems = false
        }
        view_other_items.view_other_textview.text =
            if (showExcessSelectedItems) getString(R.string.view_fewer, itemString)
            else getString(R.string.view_other, itemString)
        selected_items.list_subtitle.text = when {
            selectedItems.isEmpty() && isInterest -> getString(R.string.select_one_interest)
            selectedItems.isEmpty() && !isInterest -> getString(R.string.select_one_group)
            else -> getString(R.string.deselect)
        }
        val numItemsShown =
            if (showExcessSelectedItems) selectedItems.size else min(selectedItems.size, 3)
        updateListViewHeight(selected_items.item_list, numItemsShown)
        updateListViewHeight(more_items.item_list, moreItems.size)
    }

    /**
     * Updates the height of `listView` to show `listSize` items. Use this if the entire page needs
     * to scroll without the individually nested ListViews scrolling
     */
    private fun updateListViewHeight(listView: ListView, listSize: Int) {
        listView.layoutParams = (listView.layoutParams as LinearLayout.LayoutParams).apply {
            val displayMetrics = Resources.getSystem().displayMetrics
            val cellHeight =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, displayMetrics).toInt()

            height = cellHeight * listSize
            listView.requestLayout()
            scrollView.requestLayout()
        }
    }

    /**
     * Moves item at position `pos` in `source` to `dest`, ensuring that items are sorted in
     * alphabetical order, then updates the page to reflect changes
     */
    private fun moveItem(
        pos: Int,
        source: ArrayList<UserField>,
        dest: ArrayList<UserField>
    ) {
        val item = source.removeAt(pos)
        // find insertion index that preserves alphabetical order
        val index = max(dest.indexOfLast { i -> i.getText() < item.getText() } + 1, 0)
        dest.add(index, item)
        selectedItemsAdapter.notifyDataSetChanged()
        moreItemsAdapter.notifyDataSetChanged()
        toggleSaveButton()
        updatePage()
    }

    /** Enables or disables the Save button based on the number of selected items */
    private fun toggleSaveButton() {
        if (selectedItems.isEmpty()) {
            callback!!.onSelectionEmpty()
        } else {
            callback!!.onFilledOut()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isInterest whether this fragment edits a group or interest
         * @return A new instance of fragment EditInterestsGroupsFragment
         */
        @JvmStatic
        fun newInstance(isInterest: Boolean) =
            EditInterestsGroupsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_INTEREST, isInterest)
                }
            }

        private const val IS_INTEREST = "isInterest"
    }

    private var callback: OnFilledOutListener? = null

    override fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    override fun saveInformation() {
        val idList = selectedItems.map { it.id }
        updateUserField(
            requireContext(),
            idList,
            if (isInterest) Category.INTEREST else Category.GROUP
        )
    }
}