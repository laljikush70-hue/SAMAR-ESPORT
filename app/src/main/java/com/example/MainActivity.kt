package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.VideogameAsset
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Tournament
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.admin.AdminScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.home.HomeScreen
import com.example.ui.leaderboard.LeaderboardScreen
import com.example.ui.matches.MyMatchesScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsGoldLight
import com.example.ui.theme.EsportsRedPrimary
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceCard
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary
import com.example.ui.theme.SamarEsportsTheme
import com.example.ui.tournaments.TournamentsScreen
import com.example.ui.wallet.WalletScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SamarEsportsTheme {
                AppRootContent()
            }
        }
    }
}

@Composable
fun AppRootContent() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        SamarEsportsApp(currentUser = currentUser!!)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamarEsportsApp(currentUser: FirebaseUser) {
    val viewModel: MainViewModel = viewModel()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val myRegistrations by viewModel.myRegistrations.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentUser) {
        viewModel.onUserAuthenticated(currentUser)
    }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Top Navigation Tabs definition
    val navTabs = listOf(
        NavTabItem("Home", Screen.Home, Icons.Filled.Home, Icons.Outlined.Home),
        NavTabItem("Tournaments", Screen.Tournaments, Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports),
        NavTabItem("My Matches", Screen.MyMatches, Icons.Filled.VideogameAsset, Icons.Outlined.VideogameAsset, badgeCount = myRegistrations.size),
        NavTabItem("Wallet", Screen.Wallet, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
        NavTabItem("Profile", Screen.Profile, Icons.Filled.Person, Icons.Outlined.Person)
    )

    if (currentScreen == Screen.Admin) {
        AdminScreen(viewModel = viewModel)
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, EsportsRedPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_samar_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Column {
                                Text(
                                    text = "SAMAR ESPORTS",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "FREE FIRE INDIA",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EsportsRedPrimary,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // Leaderboard button
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Leaderboard) },
                            modifier = Modifier.testTag("open_leaderboard_topbar_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Leaderboard",
                                tint = if (currentScreen == Screen.Leaderboard) EsportsGold else EsportsTextSecondary
                            )
                        }

                        // Widara Coins Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF2A1706), Color(0xFF452B09))
                                    )
                                )
                                .border(1.dp, EsportsGold.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                                .clickable { viewModel.navigateTo(Screen.Wallet) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("topbar_wallet_chip")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(EsportsGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "W", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                }
                                Text(
                                    text = "${wallet?.coinBalance ?: 0}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EsportsGoldLight
                                )
                            }
                        }

                        // Admin icon if user has admin access
                        if (isAdmin) {
                            IconButton(
                                onClick = { viewModel.navigateTo(Screen.Admin) },
                                modifier = Modifier.testTag("admin_topbar_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Panel",
                                    tint = EsportsRedPrimary
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = EsportsSurface)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = EsportsSurfaceCard,
                    contentColor = EsportsTextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_navigation_bar")
                ) {
                    navTabs.forEach { tab ->
                        val isSelected = currentScreen == tab.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(tab.screen) },
                            icon = {
                                if (tab.badgeCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = EsportsRedPrimary,
                                                contentColor = Color.White
                                            ) {
                                                Text("${tab.badgeCount}", fontSize = 10.sp)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EsportsRedPrimary,
                                selectedTextColor = EsportsRedPrimary,
                                indicatorColor = Color(0xFF2E1015),
                                unselectedIconColor = EsportsTextSecondary,
                                unselectedTextColor = EsportsTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.screen.route}")
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = EsportsBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    Screen.Home -> HomeScreen(
                        viewModel = viewModel,
                        onSelectTournament = { tourney ->
                            viewModel.navigateTo(Screen.Tournaments)
                        },
                        onAddCoinsClicked = { viewModel.navigateTo(Screen.Wallet) }
                    )
                    Screen.Tournaments -> TournamentsScreen(
                        viewModel = viewModel,
                        onAddCoinsClicked = { viewModel.navigateTo(Screen.Wallet) }
                    )
                    Screen.MyMatches -> MyMatchesScreen(viewModel = viewModel)
                    Screen.Leaderboard -> LeaderboardScreen(viewModel = viewModel)
                    Screen.Wallet -> WalletScreen(viewModel = viewModel)
                    Screen.Profile -> ProfileScreen(
                        viewModel = viewModel,
                        onSignedOut = {
                            // Handled by Firebase auth listener
                        }
                    )
                    Screen.Admin -> AdminScreen(viewModel = viewModel)
                }
            }
        }
    }
}

data class NavTabItem(
    val label: String,
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)
