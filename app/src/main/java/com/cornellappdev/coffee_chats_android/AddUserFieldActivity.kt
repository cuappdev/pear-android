package com.cornellappdev.coffee_chats_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cornellappdev.coffee_chats_android.fragments.UserFieldFragment
import com.cornellappdev.coffee_chats_android.models.UserField
import kotlinx.android.synthetic.main.activity_add_user_field.*
import kotlinx.android.synthetic.main.activity_add_user_field.fragmentContainer

class AddUserFieldActivity : AppCompatActivity(), OnFilledOutListener {

    enum class Content {
        INTEREST,
        GROUP
    }

    private lateinit var content: Content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_field)
        content = intent.extras?.getSerializable(CONTENT) as Content
        headerText.text = when (content) {
            Content.INTEREST -> getString(R.string.add_interests)
            Content.GROUP -> getString(R.string.add_groups)
        }
        backButton.setOnClickListener { onBackPressed() }
        primaryActionButton.setOnClickListener {
            val currFragment =
                supportFragmentManager.findFragmentByTag(content.name) as OnFilledOutObservable
            currFragment.saveInformation()
            onBackPressed()
        }
        val category = when (content) {
            Content.INTEREST -> UserField.Category.INTEREST
            Content.GROUP -> UserField.Category.GROUP
        }
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(
            fragmentContainer.id,
            UserFieldFragment.newInstance(
                category,
                useViewModel = true,
                hideSelectedFields = true
            ),
            content.name
        )
            .addToBackStack("ft")
            .commit()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnFilledOutObservable) {
            fragment.setOnFilledOutListener(this)
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onFilledOut() {
        primaryActionButton.isEnabled = true
    }

    override fun onSelectionEmpty() {
        primaryActionButton.isEnabled = false
    }

    companion object {
        const val CONTENT = "content"
    }
}