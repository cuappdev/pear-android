package com.cornellappdev.coffee_chats_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cornellappdev.coffee_chats_android.networking.authenticateUser
import com.cornellappdev.coffee_chats_android.networking.getUser
import com.cornellappdev.coffee_chats_android.networking.setUpNetworking
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 10032

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.web_client_id)
            .build()
        auth = Firebase.auth
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInButton = findViewById<Button>(R.id.sign_in_button)
        val dr = ContextCompat.getDrawable(this, R.drawable.google_icon)
        dr!!.setBounds(0, 0, 60, 60) //Left,Top,Right,Bottom
        signInButton.setCompoundDrawables(dr, null, null, null)
        signInButton.compoundDrawablePadding = 30
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        // The code below is for testing. Use it instead if you can't sign in
//        val personName: String? = "Preston"
//        val personEmail: String? = "pwr36@cornell.edu"
//        if (personName != null && personEmail != null) {
//            var profile = UserProfile(personName, personEmail)
//            InternalStorage.writeObject(this, "profile", profile as Object)
//        }
//
//        val intent = Intent(this, CreateProfileActivity::class.java) // added to bypass sign in
//        startActivity(intent)

        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // The Task returned from this call is always completed, no need to attach
// a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val personName: String? = account.givenName
                val personEmail: String? = account.email
                if (personName != null && personEmail != null) {
                    val index = personEmail.indexOf('@')
                    val domain: String? =
                        if (index == -1) null else personEmail.substring(index + 1)
                    if ((domain != null && domain == "cornell.edu") || personEmail == "cornellpearapp@gmail.com") {
                        CoroutineScope(Dispatchers.Main).launch {
                            // authenticate with Firebase
                            firebaseAuthWithGoogle(account.idToken!!)
                            // authenticate with our backend
                            val userSession = authenticateUser(account.idToken!!)
                            preferencesHelper.accessToken = userSession.accessToken
                            setUpNetworking(userSession.accessToken)
                            val user = getUser()
                            val intent = if (user.hasOnboarded) {
                                Intent(applicationContext, SchedulingActivity::class.java)
                            } else {
                                Intent(applicationContext, OnboardingActivity::class.java)
                            }
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Please sign in using a Cornell account",
                            Toast.LENGTH_LONG
                        ).show()
                        signOut()
                    }
                }
            }
        } catch (e: ApiException) { // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("account error", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(applicationContext, "Sign-in failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    Log.w("FirebaseAuthError", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // ...
            }
    }

    override fun onBackPressed() {
        // Pressing the back button goes to the home screen
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        super.onBackPressed()
    }
}