package com.cornellappdev.coffee_chats_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cornellappdev.coffee_chats_android.fragments.PromptsFragment
import com.cornellappdev.coffee_chats_android.fragments.UserFieldFragment
import kotlinx.android.synthetic.main.activity_add_user_field.*

class PromptsActivity : AppCompatActivity(), OnFilledOutListener, PromptsFragment.PromptsContainer {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_field)
        backButton.setOnClickListener { onBackPressed() }
        primaryActionButton.setOnClickListener { onBackPressed() }
        val editPosition = intent.extras?.getInt(EDIT_POSITION, -1) ?: -1
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft
            .replace(
                fragmentContainer.id,
                PromptsFragment.newInstance(
                    PromptsFragment.Content.EDIT_RESPONSE,
                    useSingleton = true,
                    editPosition = editPosition
                ),
                PromptsFragment.Content.EDIT_RESPONSE.toString()
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