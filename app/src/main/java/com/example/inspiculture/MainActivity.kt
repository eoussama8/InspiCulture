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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

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

        setContent {
            InspiCultureTheme {
                var selectedTab by remember { mutableStateOf(0) }

                val booksViewModel: BooksViewModel = viewModel()
                // Observe Firebase authentication state
                auth.addAuthStateListener { authState ->
                    currentUser = authState.currentUser
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Add the EnhancedTopBar with sign-in functionality
                    EnhancedTopBar(
                        user = currentUser,
                        signInAction = { signIn() }
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        // Display different screens based on selectedTab
                        when (selectedTab) {
                            0 -> HomeScreen()
                            1 -> BooksScreen(viewModel = booksViewModel)
                            2 -> ShowsScreen()
                            3 -> MusicScreen()
                            4 -> SettingsScreen()
                        }
                    }

                    // Bottom navigation bar
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

@Composable
fun SignInScreen(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onSignInClick) {
            Text(text = "Sign In with Google")
        }
    }
}

@Composable
fun UserProfileScreen(user: FirebaseUser) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = user.photoUrl,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Hello, ${user.displayName}", fontSize = 24.sp)
        Text(text = "${user.email}", fontSize = 16.sp)
    }
}
