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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inspiculture.BooksScreen.BookDetailsScreen
import com.example.inspiculture.BooksScreen.BooksScreen
import com.example.inspiculture.HomeScreen.HomeScreen
import com.example.inspiculture.MusicScreen.MusicScreen
import com.example.inspiculture.MusicScreen.TrackDetailsScreen
import com.example.inspiculture.Retrofite.Books.Book
import com.example.inspiculture.Retrofite.Music.Track
import com.example.inspiculture.Retrofite.Shows.Show
//import com.example.inspiculture.Saves.SavedBooksScreen
import com.example.inspiculture.SettingsScreen.SettingsScreen
import com.example.inspiculture.ShowsScreen.ShowDetailsScreen
import com.example.inspiculture.ShowsScreen.ShowsScreen
import com.example.inspiculture.data.ThemePreferences
import com.example.inspiculture.ui.theme.InspiCultureTheme
import com.example.inspiculture.viewModel.BooksViewModel
import com.example.inspiculture.viewModel.MusicViewModel
import com.example.inspiculture.viewModel.ShowsViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var themePreferences: ThemePreferences

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        themePreferences = ThemePreferences(this)

        setContent {
            InspiCultureTheme(themePreferences = themePreferences) {
                MainScreen(
                    booksViewModel = viewModel(),
                    showsViewModel = viewModel(),
                    musicViewModel = viewModel(),
                    themePreferences = themePreferences,
                    signInAction = { signIn() },
                    signOutAction = {
                        auth.signOut()
                        googleSignInClient.signOut()
                    },
                    deleteAccountAction = { deleteUserAccount() }
                )
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

    private fun deleteUserAccount() {
        val user = auth.currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("MainActivity", "User account deleted.")
                    googleSignInClient.signOut()
                } else {
                    Log.e("MainActivity", "Account deletion failed: ${task.exception?.message}")
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    Log.w("MainActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }
}

@Composable
fun MainScreen(
    booksViewModel: BooksViewModel,
    showsViewModel: ShowsViewModel,
    musicViewModel: MusicViewModel,
    themePreferences: ThemePreferences,
    signInAction: () -> Unit,
    signOutAction: () -> Unit,
    deleteAccountAction: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main(selectedTab)) }
    var isSidebarOpen by remember { mutableStateOf(false) }
    val user = FirebaseAuth.getInstance().currentUser

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            EnhancedTopBar(
                user = user,
                signInAction = signInAction,
                onDetailsClick = { isSidebarOpen = true }
            )

            Box(modifier = Modifier.weight(1f)) {
                when (val screen = currentScreen) {
                    is Screen.Main -> {
                        when (screen.tabIndex) {
                            0 -> HomeScreen(
                                booksViewModel = booksViewModel,
                                showsViewModel = showsViewModel,
                                musicViewModel = musicViewModel,
                                onTabChange = { index ->
                                    selectedTab = index
                                    currentScreen = Screen.Main(index)
                                },
                                onDetailsClick = { id, type ->
                                    Log.d("HomeScreen", "Details clicked: $type with id $id")
                                }
                            )
                            1 -> BooksScreen(
                                viewModel = booksViewModel,
                                onDetailsClick = { book ->
                                    currentScreen = Screen.BookDetails(book)
                                }
                            )
                            2 -> ShowsScreen(
                                viewModel = showsViewModel,
                                onDetailsClick = { show ->
                                    currentScreen = Screen.ShowDetails(show)
                                }
                            )
                            3 -> MusicScreen(
                                viewModel = musicViewModel,
                                onDetailsClick = { track ->
                                    currentScreen = Screen.TrackDetails(track)
                                }
                            )
                            4 -> SettingsScreen(
                                themePreferences = themePreferences,
                                onDeleteGoogleAccount = deleteAccountAction
                            )
//                            5 -> SavedBooksScreen()
                        }
                    }
                    is Screen.BookDetails -> {
                        BookDetailsScreen(
                            book = screen.book,
                            onBackClick = {
                                currentScreen = Screen.Main(selectedTab)
                            }
                        )
                    }
                    is Screen.ShowDetails -> {
                        ShowDetailsScreen(
                            viewModel = showsViewModel,
                            show = screen.show,
                            onBackClick = { currentScreen = Screen.Main(selectedTab) }
                        )
                    }
                    is Screen.TrackDetails -> {
                        TrackDetailsScreen(
                            viewModel = musicViewModel,
                            track = screen.track,
                            onBackClick = { currentScreen = Screen.Main(selectedTab) },
                            onToggleFavorite = { /* Implement if needed */ }
                        )
                    }
                }
            }

            if (currentScreen is Screen.Main) {
                EnhancedTabNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        currentScreen = Screen.Main(index)
                    }
                )
            }
        }

        InspiCultureSidebar(
            isOpen = isSidebarOpen,
            user = user,
            onSavedBooksClick = {
                isSidebarOpen = false
                selectedTab = 5
                currentScreen = Screen.Main(1)
            },
            onSavedFilmsClick = {
                isSidebarOpen = false
                selectedTab = 2
                currentScreen = Screen.Main(2)
            },
            onSavedMusicClick = {
                isSidebarOpen = false
                selectedTab = 3
                currentScreen = Screen.Main(3)
            },
            onLogoutClick = {
                signOutAction()
                isSidebarOpen = false
            },
            onClose = { isSidebarOpen = false }
        )
    }
}

sealed class Screen {
    data class Main(val tabIndex: Int) : Screen()
    data class BookDetails(val book: Book) : Screen()
    data class ShowDetails(val show: Show) : Screen()
    data class TrackDetails(val track: Track) : Screen()
}