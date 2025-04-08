package com.example.inspiculture

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import com.example.inspiculture.BooksScreen.BooksScreen
import com.example.inspiculture.HomeScreen.HomeScreen
import com.example.inspiculture.MusicScreen.MusicScreen
import com.example.inspiculture.SettingsScreen.SettingsScreen
import com.example.inspiculture.ShowsScreen.ShowsScreen
import com.example.inspiculture.ui.theme.InspiCultureTheme
import com.example.inspiculture.viewModel.BooksViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.common.api.ApiException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inspiculture.Retrofite.Books.Book
import androidx.compose.runtime.livedata.observeAsState
import com.example.inspiculture.data.ThemePreferences
import com.example.inspiculture.viewModel.ShowsViewModel
import com.example.inspiculture.viewModel.MusicViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var themePreferences: ThemePreferences
    @Inject lateinit var booksViewModel: BooksViewModel
    @Inject lateinit var showsViewModel: ShowsViewModel
    @Inject lateinit var musicViewModel: MusicViewModel
    var currentUser by mutableStateOf<FirebaseUser?>(null)

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // OAuth 2.0 Client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        themePreferences = ThemePreferences(this)

        setContent {
            InspiCultureTheme(themePreferences = themePreferences) {
                var selectedTab by remember { mutableStateOf(0) }
                booksViewModel  = viewModel()
                showsViewModel  = viewModel()
                musicViewModel =viewModel()
                auth.addAuthStateListener { authState ->
                    currentUser = authState.currentUser
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    EnhancedTopBar(
                        user = currentUser,
                        signInAction = { signIn() }
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            0 -> HomeScreen()
                            1 -> BooksScreen(viewModel = booksViewModel)
                            2 -> ShowsScreen(viewModel = showsViewModel)
                            3 -> MusicScreen(viewModel = musicViewModel)
                            4 -> SettingsScreen(themePreferences = themePreferences) // Pass themePreferences here
                        }
                    }

                    EnhancedTabNavigation(
                        selectedTab = selectedTab,
                        onTabSelected = { index -> selectedTab = index }
                    )
                }
            }
        }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(task: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("MainActivity", "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    currentUser = auth.currentUser // Update the user state here
                } else {
                    Log.w("MainActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }
}

