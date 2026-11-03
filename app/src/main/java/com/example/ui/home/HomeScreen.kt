package com.example.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Tournament
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsGoldLight
import com.example.ui.theme.EsportsRedGlow
import com.example.ui.theme.EsportsRedPrimary
import com.example.ui.theme.EsportsRedVariant
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceCard
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextMuted
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onSelectTournament: (Tournament) -> Unit,
    onAddCoinsClicked: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val tournaments by viewModel.tournaments.collectAsState()
    val results by viewModel.results.collectAsState()

    var selectedCategory by remember { mutableStateOf("ALL") }

    val filteredTournaments = remember(tournaments, selectedCategory) {
        if (selectedCategory == "ALL") tournaments
        else tournaments.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Top Header & Wallet Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EsportsBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = EsportsSurfaceCard
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Welcome back, ${userProfile?.inGameName?.ifBlank { userProfile?.name } ?: "Warrior"}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EsportsTextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "UID: ${userProfile?.freeFireUid?.ifBlank { "Unlinked" }}",
                                fontSize = 12.sp,
                                color = EsportsRedPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Widara Coin pill button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF2E1C05), Color(0xFF4A2F08))
                                )
                            )
                            .border(1.dp, EsportsGold.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                            .clickable { onAddCoinsClicked() }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(EsportsGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "W",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "${wallet?.coinBalance ?: 0}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EsportsGoldLight
                            )
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Coins",
                                tint = EsportsGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Hero Tournament Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, EsportsRedPrimary.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                    .clickable { viewModel.navigateTo(Screen.Tournaments) },
                shape = RoundedCornerShape(18.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_ff_tournament),
                        contentDescription = "Esports Grand Finals Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    // Live badge & CTA
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EsportsRedPrimary)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SEASON CHAMPIONSHIP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Free Fire Mega Cup • 10,000 Widara Coins",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Register squad before slots fill up",
                            fontSize = 11.sp,
                            color = EsportsTextSecondary
                        )
                    }
                }
            }
        }

        // 3. Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "SOLO", "DUO", "SQUAD").forEach { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) EsportsRedPrimary
                                else EsportsSurface
                            )
                            .border(
                                1.dp,
                                if (isSelected) EsportsRedGlow else EsportsBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else EsportsTextSecondary
                        )
                    }
                }
            }
        }

        // 4. Tournaments Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FEATURED TOURNAMENTS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EsportsTextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "View All (${tournaments.size})",
                    fontSize = 12.sp,
                    color = EsportsRedPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Tournaments) }
                )
            }
        }

        // 5. Tournament List
        if (filteredTournaments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = EsportsSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = EsportsTextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No $selectedCategory tournaments active right now",
                            fontSize = 13.sp,
                            color = EsportsTextSecondary
                        )
                    }
                }
            }
        } else {
            items(filteredTournaments.take(4)) { tourney ->
                TournamentCard(
                    tournament = tourney,
                    onJoinClicked = { onSelectTournament(tourney) }
                )
            }
        }

        // 6. Recent Winner Highlight
        if (results.isNotEmpty()) {
            item {
                val latest = results.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EsportsGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E170A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EsportsGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "LATEST CHAMPION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = EsportsGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${latest.winnerInGameName} (${latest.winnerName})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Won ${latest.prizeAmount} Widara Coins • ${latest.tournamentName}",
                                fontSize = 11.sp,
                                color = EsportsGoldLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentCard(
    tournament: Tournament,
    onJoinClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EsportsBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Category Pill & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2A1017))
                            .border(1.dp, EsportsRedPrimary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tournament.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EsportsRedPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = EsportsTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tournament.matchDateTime,
                            fontSize = 11.sp,
                            color = EsportsTextSecondary
                        )
                    }
                }

                // Status pill
                val isUpcoming = tournament.status == "UPCOMING"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isUpcoming) Color(0xFF0F291E) else Color(0xFF2A1017)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tournament.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUpcoming) Color(0xFF00E676) else EsportsRedPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tournament Title
            Text(
                text = tournament.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EsportsTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stats grid: Prize Pool & Entry Fee
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(EsportsSurfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "PRIZE POOL", fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${tournament.prizePool} Coins",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EsportsGold
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(EsportsBorder)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "ENTRY FEE", fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${tournament.entryFee} Coins",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EsportsRedPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(EsportsBorder)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "SLOTS", fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${tournament.joinedSlots}/${tournament.maxSlots}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EsportsTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slot Progress Bar
            val progress = remember(tournament.joinedSlots, tournament.maxSlots) {
                if (tournament.maxSlots > 0) tournament.joinedSlots.toFloat() / tournament.maxSlots.toFloat()
                else 0f
            }
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EsportsRedPrimary,
                trackColor = Color(0xFF261017)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            val isFull = tournament.joinedSlots >= tournament.maxSlots
            Button(
                onClick = onJoinClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("join_tournament_button_${tournament.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFull) Color(0xFF333333) else EsportsRedPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                enabled = !isFull && tournament.status == "UPCOMING"
            ) {
                Text(
                    text = if (isFull) "MATCH FULL" else "JOIN TOURNAMENT (${tournament.entryFee} COINS)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
