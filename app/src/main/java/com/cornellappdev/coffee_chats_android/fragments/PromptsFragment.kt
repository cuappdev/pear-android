package com.cornellappdev.coffee_chats_android.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.cornellappdev.coffee_chats_android.OnFilledOutListener
import com.cornellappdev.coffee_chats_android.OnFilledOutObservable
import com.cornellappdev.coffee_chats_android.R
import com.cornellappdev.coffee_chats_android.adapters.PromptsAdapter
import com.cornellappdev.coffee_chats_android.adapters.UserFieldAdapter
import com.cornellappdev.coffee_chats_android.models.Prompt
import com.cornellappdev.coffee_chats_android.models.UserField
import com.cornellappdev.coffee_chats_android.networking.getAllPrompts
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.cornellappdev.coffee_chats_android.networking.updatePrompts
import kotlinx.android.synthetic.main.fragment_interests_groups.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromptsFragment : Fragment(), OnFilledOutObservable, PromptsAdapter.PromptsActionListener {

    var content = Content.DISPLAY_RESPONSES

    enum class Content {
        DISPLAY_RESPONSES,
        DISPLAY_PROMPTS,
        EDIT_RESPONSE
    }

    /** position of item currently being edited */
    private var editPosition = -1

    /** prompt user is currently responding to */
    private var currentPrompt: String = ""
    private lateinit var prompts: Array<UserField>

    /** prompts and user responses */
    private val responseAdapterArray = Array(3) { UserField() }

    /** whether the user is on the EDIT_RESPONSE page because they're editing an existing response */
    private var editExistingResponse = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_interests_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        group_search.visibility = View.GONE
        callback!!.onSelectionEmpty()
        CoroutineScope(Dispatchers.Main).launch {
            prompts =
                getAllPrompts().map { p -> UserField(text = p.name, id = p.id) }.toTypedArray()
            val selectedPrompts = getUser().prompts.map { p ->
                UserField(
                    text = p.name,
                    subtext = p.answer,
                    id = p.id
                )
            }.toTypedArray()
            selectedPrompts.copyInto(responseAdapterArray)
            setUpPage()
        }
    }

    private fun setUpPage() {
        when (content) {
            Content.DISPLAY_RESPONSES -> {
                container.setHeaderText(getString(R.string.prompts_header))
                interests_or_groups.adapter =
                    PromptsAdapter(requireContext(), responseAdapterArray, this)
                // only move on to next step once at least one prompt has a response
                if (responseAdapterArray.filterNot {
                        it.getText().isEmpty()
                    }.isNotEmpty()) {
                    callback!!.onFilledOut()
                } else {
                    callback!!.onSelectionEmpty()
                }
            }
            Content.DISPLAY_PROMPTS -> {
                container.setHeaderText(getString(R.string.select_prompt))
                // display only prompts that haven't been selected
                interests_or_groups.adapter = UserFieldAdapter(
                    requireContext(),
                    prompts.filterNot { it.isSelected() },
                    UserFieldAdapter.ItemColor.WHITE,
                    true
                )
                interests_or_groups.setOnItemClickListener { _, view, _, _ ->
                    val selectedView =
                        view.findViewById<ConstraintLayout>(R.id.group_or_interest_box)
                    val selectedText =
                        selectedView.findViewById<TextView>(R.id.group_or_interest_text).text
                    currentPrompt = selectedText as String
                    editExistingResponse = false
                    content = Content.EDIT_RESPONSE
                    setUpPage()
                }
            }
            Content.EDIT_RESPONSE -> {
                container.setHeaderText(getString(R.string.enter_response))
                prompt.text = currentPrompt
                if (!editExistingResponse) {
                    char_count.text = "$MAX_CHARACTERS"
                }
                if (response_edit_text.text.isNotEmpty()) {
                    callback!!.onFilledOut()
                } else {
                    callback!!.onSelectionEmpty()
                }
                // update character count
                response_edit_text.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {}

                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (s.isNotEmpty()) {
                            callback!!.onFilledOut()
                        } else {
                            callback!!.onSelectionEmpty()
                        }
                        char_count.text = "${MAX_CHARACTERS - s.length}"
                    }
                })
            }
        }
        if (content == Content.EDIT_RESPONSE) {
            interests_or_groups.visibility = View.GONE
            edit_prompt_view.visibility = View.VISIBLE
            container.setActionButtonText(getString(R.string.save))
        } else {
            interests_or_groups.visibility = View.VISIBLE
            edit_prompt_view.visibility = View.GONE
            container.setActionButtonText(getString(R.string.next))
        }
        container.setActionButtonVisibility(content != Content.DISPLAY_PROMPTS)
    }

    fun onBackPressed() {
        content = if (content == Content.EDIT_RESPONSE) {
            if (editExistingResponse) Content.DISPLAY_RESPONSES else Content.DISPLAY_PROMPTS
        } else {
            Content.DISPLAY_RESPONSES
        }
        setUpPage()
    }

    /** Saves user response to the current prompt */
    fun saveCurrentPromptResponse() {
        val prompt = prompts.filter { it.getText() == currentPrompt }[0]
        responseAdapterArray[editPosition] =
            UserField(currentPrompt, response_edit_text.text.toString(), id = prompt.id)
        prompt.setSelected()
        content = Content.DISPLAY_RESPONSES
        setUpPage()
    }

    private var callback: OnFilledOutListener? = null
    private lateinit var container: PromptsContainer

    fun setContainer(container: PromptsContainer) {
        this.container = container
    }

    override fun setOnFilledOutListener(callback: OnFilledOutListener) {
        this.callback = callback
    }

    override fun saveInformation() {
        CoroutineScope(Dispatchers.Main).launch {
            updatePrompts(
                responseAdapterArray.toList().map { Prompt(answer = it.getSubtext(), id = it.id) })
        }
    }

    override fun onAddPrompt(position: Int) {
        content = Content.DISPLAY_PROMPTS
        editPosition = position
        response_edit_text.text.clear()
        setUpPage()
    }

    override fun onClearPrompt(position: Int) {
        prompts.filter { it.getText() == responseAdapterArray[position].getText() }[0].toggleSelected()
        responseAdapterArray[position] = UserField()
        setUpPage()
    }

    override fun onEditPrompt(position: Int) {
        val field = responseAdapterArray[position]
        currentPrompt = field.getText()
        response_edit_text.setText(field.getSubtext())
        char_count.text = "${MAX_CHARACTERS - field.getSubtext().length}"
        editPosition = position
        content = Content.EDIT_RESPONSE
        editExistingResponse = true
        setUpPage()
    }

    interface PromptsContainer {
        fun setActionButtonText(text: String)

        fun setActionButtonVisibility(isVisible: Boolean)

        fun setHeaderText(text: String)
    }

    companion object {
        private const val MAX_CHARACTERS = 150
    }
}