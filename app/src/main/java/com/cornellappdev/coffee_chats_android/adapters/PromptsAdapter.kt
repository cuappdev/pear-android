package com.cornellappdev.coffee_chats_android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.models.UserField
import kotlinx.android.synthetic.main.prompt_with_response_item.view.*

class PromptsAdapter(
    private val mContext: Context,
    private val promptList: Array<UserField>,
    private val callback: PromptsActionListener
) :
    ArrayAdapter<UserField>(mContext, 0, promptList) {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val listItem = convertView
            ?: LayoutInflater.from(mContext)
                .inflate(R.layout.prompt_with_response_item, parent, false)

        val currentItem = promptList[position]
        // customize appearance based on whether this cell contains a response
        if (currentItem.getSubtext().isNotEmpty()) {
            listItem.prompt_header.visibility = View.VISIBLE
            listItem.clear_icon.setOnClickListener { callback.onClearPrompt(position) }
            listItem.prompt.text = currentItem.getText()
            listItem.prompt_response.setTextColor(mContext.getColor(R.color.black))
            listItem.prompt_response.text = currentItem.getSubtext()
            listItem.add_response.visibility = View.GONE
            listItem.setOnClickListener { callback.onEditPrompt(position) }
        } else {
            listItem.prompt_header.visibility = View.GONE
            listItem.prompt_response.text = mContext.getString(R.string.select_prompt)
            listItem.prompt_response.setTextColor(mContext.getColor(R.color.onboardingButtonDisabled))
            listItem.add_response.visibility = View.VISIBLE
            listItem.setOnClickListener { callback.onAddPrompt(position) }
        }

        return listItem
    }

    interface PromptsActionListener {
        /**
         * Starts flow for adding a prompt response at index `position`
         */
        fun onAddPrompt(position: Int)

        /**
         * Clears the prompt and response at index `position`
         */
        fun onClearPrompt(position: Int)

        /**
         * Starts flow for editing prompt at index `position`
         */
        fun onEditPrompt(position: Int)
    }
}