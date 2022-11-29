package com.cornellappdev.coffee_chats_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cornellappdev.coffee_chats_android.fragments.PromptsFragment
import com.cornellappdev.coffee_chats_android.fragments.UserFieldFragment
import com.cornellappdev.coffee_chats_android.singletons.UserSingleton
import kotlinx.android.synthetic.main.activity_add_user_field.*

class PromptsActivity : AppCompatActivity(), OnFilledOutListener, PromptsFragment.PromptsContainer {

    private var content = PromptsFragment.Content.DISPLAY_PROMPTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_field)
        val editPosition = intent.extras?.getInt(EDIT_POSITION, -1) ?: -1
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        content =
            if (editPosition == -1) PromptsFragment.Content.DISPLAY_PROMPTS
            else PromptsFragment.Content.EDIT_RESPONSE
        backButton.setOnClickListener { onBackPressed() }
        primaryActionButton.setOnClickListener {
            if (content == PromptsFragment.Content.EDIT_RESPONSE) {
                val currFragment =
                    supportFragmentManager.findFragmentByTag(content.name) as OnFilledOutObservable
                currFragment.saveInformation()
            }
            UserSingleton.saveUserInfo()
            onBackPressed()
        }
        ft
            .replace(
                fragmentContainer.id,
                PromptsFragment.newInstance(
                    content,
                    useSingleton = true,
                    editPosition = editPosition
                ),
                content.toString()
            )
            .commit()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnFilledOutObservable) {
            fragment.setOnFilledOutListener(this)
        }
        if (fragment is PromptsFragment) {
            fragment.setContainer(this)
        }
    }

    override fun setActionButtonText(text: String) {
        // TODO implement
    }

    override fun setActionButtonVisibility(isVisible: Boolean) {
        primaryActionButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setHeaderText(text: String) {
        headerText.text = text
    }

    override fun onFilledOut() {
        // TODO Implement
    }

    override fun onSelectionEmpty() {
        // TODO Implement
    }

    companion object {
        const val EDIT_POSITION = "EDIT_POSITION"
    }
}