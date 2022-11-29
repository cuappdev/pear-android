package com.cornellappdev.coffee_chats_android.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.*
import com.cornellappdev.coffee_chats_android.adapters.UserFieldAdapter
import com.cornellappdev.coffee_chats_android.adapters.UserFieldAdapter.ItemColor
import com.cornellappdev.coffee_chats_android.models.UserField
import com.cornellappdev.coffee_chats_android.models.UserField.Category
import com.cornellappdev.coffee_chats_android.singletons.UserSingleton
import kotlinx.android.synthetic.main.fragment_edit_interests.*
import kotlinx.android.synthetic.main.fragment_edit_interests.view.*
import kotlinx.android.synthetic.main.interest_group_list_with_header.view.*
import kotlin.math.min

class EditInterestsGroupsFragment : Fragment(), OnFilledOutObservable {
    private var isInterest = true
    private lateinit var itemString: String
    private val selectedItems = ArrayList<UserField>()
    private lateinit var selectedItemsAdapter: UserFieldAdapter

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
        add_item_button.text =
            getString(if (isInterest) R.string.add_interests else R.string.add_groups)
        add_item_button.setOnClickListener {
            val intent = Intent(context, AddUserFieldActivity::class.java).apply {
                putExtra(
                    AddUserFieldActivity.CONTENT,
                    if (isInterest) AddUserFieldActivity.Content.INTEREST else AddUserFieldActivity.Content.GROUP
                )
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        selectedItems.clear()
        if (isInterest) {
            val userInterests = UserSingleton.user.interests
            for (interest in userInterests) {
                val item = UserField(
                    interest.name,
                    interest.subtitle,
                    interest.imageUrl,
                    id = interest.id
                )
                selectedItems.add(item)
            }
        } else {
            val userGroups = UserSingleton.user.groups
            for (group in userGroups) {
                val item = UserField(group.name, drawableUrl = group.imageUrl, id = group.id)
                selectedItems.add(item)
            }
        }

        selectedItemsAdapter =
            UserFieldAdapter(
                requireContext(),
                selectedItems,
                ItemColor.WHITE,
                hideIcon = false,
                showDeleteIcon = true,
                onDeleteClickListener = { pos: Int -> removeItem(pos) }
            )
        selected_items.item_list.adapter = selectedItemsAdapter
        view_other_items.setOnClickListener {
            showExcessSelectedItems = !showExcessSelectedItems
            updatePage()
        }
        toggleSaveButton()
        updatePage()
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
    }

    /**
     * Updates the height of `listView` to show `listSize` items. Use this if the entire page needs
     * to scroll without the individually nested ListViews scrolling
     */
    private fun updateListViewHeight(listView: ListView, listSize: Int) {
        listView.layoutParams = (listView.layoutParams as LinearLayout.LayoutParams).apply {
            val cellHeight = dpToPixels(requireContext(), 80)
            height = cellHeight * listSize
            listView.requestLayout()
            scrollView.requestLayout()
        }
    }

    /**
     * Removes item at position `pos` in `selectedItems`, saves changes to the repository, then
     * updates the page to reflect changes
     */
    private fun removeItem(pos: Int) {
        val removedItemId = selectedItems.removeAt(pos).id
        if (isInterest) {
            UserSingleton.removeInterest(removedItemId)
        } else {
            UserSingleton.removeGroup(removedItemId)
        }
        selectedItemsAdapter.notifyDataSetChanged()
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