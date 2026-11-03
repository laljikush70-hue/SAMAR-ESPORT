package com.example.ui.tournaments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Tournament
import com.example.ui.MainViewModel
import com.example.ui.home.TournamentCard
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsRedGlow
import com.example.ui.theme.EsportsRedPrimary
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceCard
import com.example.ui.theme.EsportsTextMuted
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun TournamentsScreen(
    viewModel: MainViewModel,
    onAddCoinsClicked: () -> Unit
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val categories = listOf("ALL", "SOLO", "DUO", "SQUAD")

    var selectedTournamentForJoin by remember { mutableStateOf<Tournament?>(null) }
    var viewingRulesTournament by remember { mutableStateOf<Tournament?>(null) }

    val filtered = remember(tournaments, selectedTab) {
        if (selectedTab == 0) tournaments
        else tournaments.filter { it.category.equals(categories[selectedTab], ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = EsportsSurface,
            contentColor = EsportsRedPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = EsportsRedPrimary,
                    height = 3.dp
                )
            }
        ) {
            categories.forEachIndexed { index, cat ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = cat,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) EsportsRedPrimary else EsportsTextSecondary
                        )
                    }
                )
            }
        }

        // Tournaments list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = EsportsTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No ${categories[selectedTab]} matches available",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EsportsTextPrimary
                            )
                            Text(
                                text = "Check back soon for upcoming Free Fire tournament rooms!",
                                fontSize = 12.sp,
                                color = EsportsTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filtered) { tourney ->
                    TournamentCard(
                        tournament = tourney,
                        onJoinClicked = { selectedTournamentForJoin = tourney }
                    )
                }
            }
        }
    }

    // Join Tournament Dialog
    selectedTournamentForJoin?.let { tournament ->
        var playerUid by remember { mutableStateOf(userProfile?.freeFireUid ?: "") }
        var inGameName by remember { mutableStateOf(userProfile?.inGameName ?: "") }
        var teammate1 by remember { mutableStateOf("") }
        var teammate2 by remember { mutableStateOf("") }
        var teammate3 by remember { mutableStateOf("") }

        val userCoins = wallet?.coinBalance ?: 0L
        val hasEnoughCoins = userCoins >= tournament.entryFee

        AlertDialog(
            onDismissRequest = { selectedTournamentForJoin = null },
            containerColor = EsportsSurface,
            title = {
                Text(
                    text = "Join ${tournament.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EsportsTextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cost & Balance Indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (hasEnoughCoins) Color(0xFF14241B) else Color(0xFF291417))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Entry Fee", fontSize = 11.sp, color = EsportsTextSecondary)
                            Text(
                                text = "${tournament.entryFee} Widara Coins",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EsportsGold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Your Balance", fontSize = 11.sp, color = EsportsTextSecondary)
                            Text(
                                text = "$userCoins Coins",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasEnoughCoins) Color(0xFF00E676) else EsportsRedPrimary
                            )
                        }
                    }

                    if (!hasEnoughCoins) {
                        Text(
                            text = "You need ${tournament.entryFee - userCoins} more coins to join. Add coins to your wallet to continue.",
                            fontSize = 12.sp,
                            color = EsportsRedPrimary
                        )
                    }

                    OutlinedTextField(
                        value = playerUid,
                        onValueChange = { playerUid = it },
                        label = { Text("Free Fire Player UID") },
                        placeholder = { Text("e.g. 192837465") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("join_player_uid_input"),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = inGameName,
                        onValueChange = { inGameName = it },
                        label = { Text("In-Game Name (IGN)") },
                        placeholder = { Text("e.g. SAMAR_SNIPER") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("join_player_ign_input"),
                        colors = outlinedTextFieldColors()
                    )

                    if (tournament.category == "DUO" || tournament.category == "SQUAD") {
                        OutlinedTextField(
                            value = teammate1,
                            onValueChange = { teammate1 = it },
                            label = { Text("Teammate 1 (UID / IGN)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = outlinedTextFieldColors()
                        )
                    }

                    if (tournament.category == "SQUAD") {
                        OutlinedTextField(
                            value = teammate2,
                            onValueChange = { teammate2 = it },
                            label = { Text("Teammate 2 (UID / IGN)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = outlinedTextFieldColors()
                        )
                        OutlinedTextField(
                            value = teammate3,
                            onValueChange = { teammate3 = it },
                            label = { Text("Teammate 3 (UID / IGN)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = outlinedTextFieldColors()
                        )
                    }
                }
            },
            confirmButton = {
                if (hasEnoughCoins) {
                    Button(
                        onClick = {
                            val teammatesList = mutableListOf<String>()
                            if (teammate1.isNotBlank()) teammatesList.add(teammate1.trim())
                            if (teammate2.isNotBlank()) teammatesList.add(teammate2.trim())
                            if (teammate3.isNotBlank()) teammatesList.add(teammate3.trim())

                            viewModel.joinTournament(
                                tournament = tournament,
                                playerUid = playerUid.trim().ifBlank { "FF${(100000..999999).random()}" },
                                inGameName = inGameName.trim().ifBlank { userProfile?.name ?: "Player" },
                                teammates = teammatesList,
                                onSuccess = {
                                    selectedTournamentForJoin = null
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EsportsRedPrimary),
                        modifier = Modifier.testTag("confirm_join_match_button")
                    ) {
                        Text("Pay & Confirm Slot", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            selectedTournamentForJoin = null
                            onAddCoinsClicked()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EsportsGold),
                        modifier = Modifier.testTag("add_coins_from_dialog_button")
                    ) {
                        Text("Add Widara Coins", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTournamentForJoin = null }) {
                    Text("Cancel", color = EsportsTextSecondary)
                }
            }
        )
    }
}

@Composable
fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EsportsRedPrimary,
    unfocusedBorderColor = EsportsBorder,
    focusedLabelColor = EsportsRedPrimary,
    unfocusedLabelColor = EsportsTextSecondary,
    focusedTextColor = EsportsTextPrimary,
    unfocusedTextColor = EsportsTextPrimary,
    cursorColor = EsportsRedPrimary
)
