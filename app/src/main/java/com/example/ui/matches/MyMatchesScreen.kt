package com.example.ui.matches

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Registration
import com.example.data.model.Tournament
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsRedPrimary
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceCard
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextMuted
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun MyMatchesScreen(
    viewModel: MainViewModel
) {
    val myRegistrations by viewModel.myRegistrations.collectAsState()
    val tournaments by viewModel.tournaments.collectAsState()
    val context = LocalContext.current

    val tourneyMap = remember(tournaments) {
        tournaments.associateBy { it.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MY MATCHES",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = EsportsTextPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Track your registered custom rooms, Room ID & Password",
            fontSize = 12.sp,
            color = EsportsTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (myRegistrations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "You haven't joined any tournaments yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EsportsTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Browse tournaments and book your slot now using Widara Coins!",
                            fontSize = 12.sp,
                            color = EsportsTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.navigateTo(Screen.Tournaments) },
                            colors = ButtonDefaults.buttonColors(containerColor = EsportsRedPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Browse Tournaments", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(myRegistrations) { reg ->
                    val tournament = tourneyMap[reg.tournamentId]
                    JoinedMatchCard(
                        registration = reg,
                        tournament = tournament,
                        onCopyRoomDetails = { id, pass ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Free Fire Room", "Room ID: $id | Password: $pass")
                            clipboard.setPrimaryClip(clip)
                            viewModel.showMessage("Room credentials copied to clipboard!")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun JoinedMatchCard(
    registration: Registration,
    tournament: Tournament?,
    onCopyRoomDetails: (String, String) -> Unit
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Category & Match Time
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
                            text = tournament?.category ?: registration.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EsportsRedPrimary
                        )
                    }

                    Text(
                        text = tournament?.matchDateTime ?: "Scheduled",
                        fontSize = 12.sp,
                        color = EsportsTextSecondary
                    )
                }

                // Verified Slot badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0E2419))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CONFIRMED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E676)
                    )
                }
            }

            // Tournament Name
            Text(
                text = tournament?.name ?: "Free Fire Tournament",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EsportsTextPrimary
            )

            // Player details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(EsportsSurfaceVariant.copy(alpha = 0.6f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "IN-GAME NAME (IGN)", fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
                    Text(text = registration.inGameName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EsportsTextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "FREE FIRE UID", fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
                    Text(text = registration.playerUid, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EsportsRedPrimary)
                }
            }

            // ROOM CREDENTIALS SECTION
            val hasRoom = tournament?.roomId?.isNotBlank() == true
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (hasRoom) Color(0xFF00E676).copy(alpha = 0.5f) else EsportsBorder,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasRoom) Color(0xFF0B1F16) else Color(0xFF1B1416)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hasRoom) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (hasRoom) Color(0xFF00E676) else EsportsTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasRoom) "ROOM ID & PASSWORD READY" else "ROOM DETAILS LOCKED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (hasRoom) Color(0xFF00E676) else EsportsTextSecondary
                            )
                        }

                        if (hasRoom) {
                            IconButton(
                                onClick = {
                                    onCopyRoomDetails(
                                        tournament!!.roomId,
                                        tournament.roomPassword
                                    )
                                },
                                modifier = Modifier.size(28.dp).testTag("copy_room_details_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (hasRoom) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Room ID", fontSize = 11.sp, color = EsportsTextSecondary)
                                Text(
                                    text = tournament!!.roomId,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Password", fontSize = 11.sp, color = EsportsTextSecondary)
                                Text(
                                    text = tournament!!.roomPassword,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        Text(
                            text = "Open Free Fire > Custom Room > Enter Room ID and join!",
                            fontSize = 11.sp,
                            color = Color(0xFF81C784),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Text(
                            text = "Room ID and Room Password will be posted here 15 minutes before match start time. Keep the app open!",
                            fontSize = 11.sp,
                            color = EsportsTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
