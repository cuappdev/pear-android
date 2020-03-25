package com.cornellappdev.coffee_chats_android

//import android.support.v7.app.AppCompatActivity

//import androidx.test.orchestrator.junit.BundleJUnitUtils.getResult

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cornellappdev.coffee_chats_android.models.InternalStorage
import com.cornellappdev.coffee_chats_android.models.UserProfile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class SignInActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 10032

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.web_client_id)
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInButton = findViewById<Button>(R.id.sign_in_button)

        val dr =
            resources.getDrawable(R.drawable.google_icon)
        dr.setBounds(0, 0, 60, 60) //Left,Top,Right,Bottom
        signInButton.setCompoundDrawables(dr, null, null, null)
        signInButton.compoundDrawablePadding = 30


        signInButton.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
    }
    private fun signIn() {
        val personName: String? = "Preston"
        val personEmail: String? = "pwr36@cornell.edu"
        if (personName != null && personEmail != null) {
            var profile = UserProfile(personName, personEmail)
            InternalStorage.writeObject(this, "profile", profile as Object)
        }

        val intent = Intent(this, CreateProfileActivity::class.java) // added to bypass sign in
        startActivity(intent)

//        val signInIntent: Intent = mGoogleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // The Task returned from this call is always completed, no need to attach
// a listener.
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)
            val intent = Intent(this, CreateProfileActivity::class.java)
            if (account != null) {
                val personName: String? = account.givenName
                val personEmail: String? = account.email
                if (personName != null && personEmail != null) {
                    val index = personEmail.indexOf('@')
                    val domain: String? = if (index == -1) null else personEmail.substring(index + 1)
                    if (domain != null && domain.equals("cornell.edu")) {
                        var profile = UserProfile(personName, personEmail)
                        InternalStorage.writeObject(this, "profile", profile as Object)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Please sign in using a Cornell account", Toast.LENGTH_LONG).show();
                        signOut()
                    }
                }
            }

        } catch (e: ApiException) { // The ApiException status code indicates the detailed failure reason.
// Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("account error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(applicationContext, "Sign-in failed", Toast.LENGTH_LONG).show();
        }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // ...
            }
    }
}
