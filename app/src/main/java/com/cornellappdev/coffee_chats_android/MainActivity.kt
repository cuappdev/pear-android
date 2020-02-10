package com.cornellappdev.coffee_chats_android

//import android.support.v7.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
//import androidx.test.orchestrator.junit.BundleJUnitUtils.getResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
//import sun.jvm.hotspot.utilities.IntArray


//import sun.jvm.hotspot.utilities.IntArray


//import sun.jvm.hotspot.utilities.IntArray



class MainActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 10032

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.web_client_id)
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        updateUI(account)

        val signInButton = findViewById<Button>(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // The Task returned from this call is always completed, no need to attach
// a listener.
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("account", task.toString())
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            Log.d("account", account.toString())
        } catch (e: ApiException) { // The ApiException status code indicates the detailed failure reason.
// Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("account failed", e.toString())
//            updateUI(null)
        }
    }
}
