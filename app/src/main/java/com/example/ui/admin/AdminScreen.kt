package com.example.ui.admin

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.data.model.ResultRecord
import com.example.data.model.Tournament
import com.example.data.model.Withdrawal
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsRedGlow
import com.example.ui.theme.EsportsRedPrimary
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceCard
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextMuted
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary
import com.example.ui.tournaments.outlinedTextFieldColors
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: MainViewModel
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val tournaments by viewModel.tournaments.collectAsState()
    val allWithdrawals by viewModel.allWithdrawals.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tournaments", "Withdrawals", "Coins Adjustment", "Publish Result")

    var showCreateTournamentDialog by remember { mutableStateOf(false) }
    var managingRoomTournament by remember { mutableStateOf<Tournament?>(null) }
    var publishingResultTournament by remember { mutableStateOf<Tournament?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SAMAR ADMIN DASHBOARD",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = EsportsRedPrimary
                        )
                        Text(
                            text = "Platform Control & Tournament Host",
                            fontSize = 11.sp,
                            color = EsportsTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (selectedAdminTab == 0) {
                        Button(
                            onClick = { showCreateTournamentDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EsportsRedPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.padding(end = 8.dp).testTag("admin_create_tournament_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Match", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EsportsSurface)
            )
        },
        containerColor = EsportsBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Analytics Overview Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1B1115))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdminStatChip("Total Tournaments", "${tournaments.size}")
                AdminStatChip("Pending Payouts", "${allWithdrawals.count { it.status == "PENDING" }}")
                AdminStatChip("Total Prize Pool", "${tournaments.sumOf { it.prizePool }} W")
            }

            // Tab bar
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = EsportsSurfaceCard,
                contentColor = EsportsRedPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedAdminTab]),
                        color = EsportsRedPrimary,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedAdminTab == index,
                        onClick = { selectedAdminTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedAdminTab == index) EsportsRedPrimary else EsportsTextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedAdminTab) {
                0 -> {
                    // Manage Tournaments
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(tournaments) { tourney ->
                            AdminTournamentCard(
                                tournament = tourney,
                                onSetRoom = { managingRoomTournament = tourney },
                                onPublishResult = { publishingResultTournament = tourney },
                                onDelete = { viewModel.adminDeleteTournament(tourney.id) }
                            )
                        }
                    }
                }
                1 -> {
                    // Process Withdrawals
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        val pending = allWithdrawals.filter { it.status == "PENDING" }
                        val others = allWithdrawals.filter { it.status != "PENDING" }

                        item {
                            Text(
                                text = "PENDING REQUESTS (${pending.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = EsportsGold
                            )
                        }

                        if (pending.isEmpty()) {
                            item {
                                Text(
                                    text = "No pending withdrawal requests right now.",
                                    fontSize = 12.sp,
                                    color = EsportsTextSecondary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        } else {
                            items(pending) { w ->
                                AdminWithdrawalActionCard(
                                    withdrawal = w,
                                    onApprove = { note -> viewModel.adminProcessWithdrawal(w, true, note) },
                                    onReject = { note -> viewModel.adminProcessWithdrawal(w, false, note) }
                                )
                            }
                        }

                        if (others.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "PROCESSED REQUESTS (${others.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EsportsTextSecondary
                                )
                            }
                            items(others) { w ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "${w.userName} • ${w.amount} Coins", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EsportsTextPrimary)
                                            Text(text = "UPI: ${w.upiIdOrBank}", fontSize = 11.sp, color = EsportsTextSecondary)
                                        }
                                        Text(
                                            text = w.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (w.status == "APPROVED") Color(0xFF00E676) else EsportsRedPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Manual Coins Adjustment
                    AdminCoinsAdjustmentTab(viewModel = viewModel)
                }
                3 -> {
                    // Publish Match Result
                    AdminPublishResultTab(
                        tournaments = tournaments,
                        onPublish = { res ->
                            viewModel.adminPublishResult(res) {}
                        }
                    )
                }
            }
        }
    }

    // Create Tournament Dialog
    if (showCreateTournamentDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("SOLO") }
        var matchTime by remember { mutableStateOf("Today, 08:00 PM IST") }
        var entryFeeText by remember { mutableStateOf("30") }
        var prizePoolText by remember { mutableStateOf("1200") }
        var maxSlotsText by remember { mutableStateOf("48") }
        var rulesText by remember { mutableStateOf("1. Bermuda Map\n2. No Emulators\n3. Screenshot of rank required") }

        AlertDialog(
            onDismissRequest = { showCreateTournamentDialog = false },
            containerColor = EsportsSurface,
            title = {
                Text(
                    text = "Create Free Fire Tournament",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EsportsTextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tournament Name") },
                        placeholder = { Text("e.g. Free Fire Squad War") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_new_tourney_name"),
                        colors = outlinedTextFieldColors()
                    )

                    // Category Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("SOLO", "DUO", "SQUAD").forEach { cat ->
                            val isSel = category == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) EsportsRedPrimary else EsportsSurfaceVariant)
                                    .clickable { category = cat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = cat, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = matchTime,
                        onValueChange = { matchTime = it },
                        label = { Text("Match Schedule Time") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = entryFeeText,
                            onValueChange = { entryFeeText = it.filter { c -> c.isDigit() } },
                            label = { Text("Entry Fee (Coins)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = outlinedTextFieldColors()
                        )
                        OutlinedTextField(
                            value = prizePoolText,
                            onValueChange = { prizePoolText = it.filter { c -> c.isDigit() } },
                            label = { Text("Prize Pool (Coins)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = outlinedTextFieldColors()
                        )
                    }

                    OutlinedTextField(
                        value = maxSlotsText,
                        onValueChange = { maxSlotsText = it.filter { c -> c.isDigit() } },
                        label = { Text("Max Slots") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = rulesText,
                        onValueChange = { rulesText = it },
                        label = { Text("Tournament Rules") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val t = Tournament(
                            id = "tourney_" + UUID.randomUUID().toString().take(8),
                            name = name.trim().ifBlank { "Free Fire $category Cup" },
                            category = category,
                            matchDateTime = matchTime.trim(),
                            entryFee = entryFeeText.toLongOrNull() ?: 30L,
                            prizePool = prizePoolText.toLongOrNull() ?: 1200L,
                            maxSlots = maxSlotsText.toLongOrNull() ?: 48L,
                            joinedSlots = 0L,
                            rules = rulesText.trim(),
                            status = "UPCOMING"
                        )
                        viewModel.adminCreateTournament(t) {
                            showCreateTournamentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EsportsRedPrimary),
                    modifier = Modifier.testTag("admin_confirm_create_tourney_btn")
                ) {
                    Text("Publish Tournament", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTournamentDialog = false }) {
                    Text("Cancel", color = EsportsTextSecondary)
                }
            }
        )
    }

    // Set Room ID & Password Modal
    managingRoomTournament?.let { tournament ->
        var roomId by remember { mutableStateOf(tournament.roomId) }
        var roomPassword by remember { mutableStateOf(tournament.roomPassword) }

        AlertDialog(
            onDismissRequest = { managingRoomTournament = null },
            containerColor = EsportsSurface,
            title = {
                Text(
                    text = "Set Custom Room Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EsportsTextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Match: ${tournament.name} (${tournament.category})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EsportsRedPrimary
                    )
                    Text(
                        text = "Once saved, Room ID and Password will be immediately visible to all registered players in 'My Matches'.",
                        fontSize = 11.sp,
                        color = EsportsTextSecondary
                    )

                    OutlinedTextField(
                        value = roomId,
                        onValueChange = { roomId = it },
                        label = { Text("Custom Room ID") },
                        placeholder = { Text("e.g. 5829104") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_room_id_input"),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = roomPassword,
                        onValueChange = { roomPassword = it },
                        label = { Text("Custom Room Password") },
                        placeholder = { Text("e.g. 1234") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_room_pass_input"),
                        colors = outlinedTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminSetRoomDetails(tournament.id, roomId, roomPassword) {
                            managingRoomTournament = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    modifier = Modifier.testTag("admin_save_room_btn")
                ) {
                    Text("Save & Broadcast", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { managingRoomTournament = null }) {
                    Text("Cancel", color = EsportsTextSecondary)
                }
            }
        )
    }
}

@Composable
fun AdminStatChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = EsportsTextSecondary)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = EsportsGold)
    }
}

@Composable
fun AdminTournamentCard(
    tournament: Tournament,
    onSetRoom: () -> Unit,
    onPublishResult: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EsportsBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${tournament.category} • ${tournament.matchDateTime}",
                    fontSize = 11.sp,
                    color = EsportsTextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tournament.status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = when (tournament.status) {
                        "UPCOMING" -> Color(0xFF00E676)
                        "ONGOING" -> EsportsGold
                        else -> EsportsTextMuted
                    }
                )
            }

            Text(
                text = tournament.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EsportsTextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Prize: ${tournament.prizePool} Coins", fontSize = 12.sp, color = EsportsGold, fontWeight = FontWeight.Bold)
                Text(text = "Entry: ${tournament.entryFee} Coins", fontSize = 12.sp, color = EsportsRedPrimary, fontWeight = FontWeight.Bold)
                Text(text = "Slots: ${tournament.joinedSlots}/${tournament.maxSlots}", fontSize = 12.sp, color = EsportsTextPrimary, fontWeight = FontWeight.Bold)
            }

            // Room status line
            val hasRoom = tournament.roomId.isNotBlank()
            Text(
                text = if (hasRoom) "Room ID: ${tournament.roomId} | Pass: ${tournament.roomPassword}" else "Room credentials not posted yet",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (hasRoom) Color(0xFF00E676) else EsportsTextSecondary
            )

            // Admin actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSetRoom,
                    modifier = Modifier.weight(1f).height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EsportsSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Set Room", fontSize = 11.sp)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = EsportsRedPrimary)
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawalActionCard(
    withdrawal: Withdrawal,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    var adminNote by remember { mutableStateOf("Approved and paid via UPI") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EsportsGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = withdrawal.userName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EsportsTextPrimary)
                    Text(text = "UPI: ${withdrawal.upiIdOrBank}", fontSize = 12.sp, color = EsportsGold)
                }
                Text(
                    text = "${withdrawal.amount} Coins (₹${withdrawal.amount})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            OutlinedTextField(
                value = adminNote,
                onValueChange = { adminNote = it },
                label = { Text("Admin Note / Transaction Ref") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onApprove(adminNote) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve & Pay", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onReject(adminNote) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EsportsRedPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject & Refund", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminCoinsAdjustmentTab(viewModel: MainViewModel) {
    var targetUserId by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("100") }
    var reasonText by remember { mutableStateOf("Match reward / compensation") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "MANUAL WIDARA COIN ADJUSTMENT",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = EsportsGold
        )
        Text(
            text = "Directly credit or debit Widara Coins to any player's wallet with an audit reason.",
            fontSize = 11.sp,
            color = EsportsTextSecondary
        )

        OutlinedTextField(
            value = targetUserId,
            onValueChange = { targetUserId = it },
            label = { Text("Player User ID (UID)") },
            placeholder = { Text("Paste player Firebase UID") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors()
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it.filter { c -> c.isDigit() } },
            label = { Text("Coin Amount") },
            placeholder = { Text("e.g. 200") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors()
        )

        OutlinedTextField(
            value = reasonText,
            onValueChange = { reasonText = it },
            label = { Text("Reason / Audit Note") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val amount = amountText.toLongOrNull() ?: 0L
                    if (targetUserId.isNotBlank() && amount > 0) {
                        viewModel.adminAdjustCoins(targetUserId.trim(), amount, true, reasonText) {
                            targetUserId = ""
                        }
                    } else {
                        viewModel.showMessage("Please fill valid Player UID and Amount")
                    }
                },
                modifier = Modifier.weight(1f).height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Credit Coins", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val amount = amountText.toLongOrNull() ?: 0L
                    if (targetUserId.isNotBlank() && amount > 0) {
                        viewModel.adminAdjustCoins(targetUserId.trim(), amount, false, reasonText) {
                            targetUserId = ""
                        }
                    } else {
                        viewModel.showMessage("Please fill valid Player UID and Amount")
                    }
                },
                modifier = Modifier.weight(1f).height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EsportsRedPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.RemoveCircle, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Deduct Coins", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminPublishResultTab(
    tournaments: List<Tournament>,
    onPublish: (ResultRecord) -> Unit
) {
    var selectedTournament by remember { mutableStateOf(tournaments.firstOrNull()) }
    var winnerUid by remember { mutableStateOf("") }
    var winnerName by remember { mutableStateOf("") }
    var winnerIgn by remember { mutableStateOf("") }
    var prizeCoinsText by remember { mutableStateOf(selectedTournament?.prizePool?.toString() ?: "1000") }
    var killPointsText by remember { mutableStateOf("120") }
    var placementPointsText by remember { mutableStateOf("200") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "PUBLISH TOURNAMENT WINNER & SCORE",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = EsportsGold
        )
        Text(
            text = "Assign tournament champion, points and automatically award prize coins to winner's wallet.",
            fontSize = 11.sp,
            color = EsportsTextSecondary
        )

        OutlinedTextField(
            value = winnerUid,
            onValueChange = { winnerUid = it },
            label = { Text("Winner User UID") },
            placeholder = { Text("Player UID who won 1st Place") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors()
        )

        OutlinedTextField(
            value = winnerName,
            onValueChange = { winnerName = it },
            label = { Text("Winner Player Name") },
            placeholder = { Text("e.g. Rahul Sharma") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors()
        )

        OutlinedTextField(
            value = winnerIgn,
            onValueChange = { winnerIgn = it },
            label = { Text("Winner In-Game Name (IGN)") },
            placeholder = { Text("e.g. SAMAR_VIPER") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = prizeCoinsText,
                onValueChange = { prizeCoinsText = it.filter { c -> c.isDigit() } },
                label = { Text("Prize Coins") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = outlinedTextFieldColors()
            )
            OutlinedTextField(
                value = killPointsText,
                onValueChange = { killPointsText = it.filter { c -> c.isDigit() } },
                label = { Text("Kill Pts") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = outlinedTextFieldColors()
            )
            OutlinedTextField(
                value = placementPointsText,
                onValueChange = { placementPointsText = it.filter { c -> c.isDigit() } },
                label = { Text("Place Pts") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = outlinedTextFieldColors()
            )
        }

        Button(
            onClick = {
                val t = selectedTournament ?: tournaments.firstOrNull()
                val prize = prizeCoinsText.toLongOrNull() ?: 1000L
                val kills = killPointsText.toLongOrNull() ?: 0L
                val place = placementPointsText.toLongOrNull() ?: 0L

                val r = ResultRecord(
                    id = "res_" + UUID.randomUUID().toString().take(8),
                    tournamentId = t?.id ?: "custom_match",
                    tournamentName = t?.name ?: "Free Fire Tournament",
                    winnerUid = winnerUid.trim().ifBlank { "winner_sample" },
                    winnerName = winnerName.trim().ifBlank { "Champion" },
                    winnerInGameName = winnerIgn.trim().ifBlank { "SAMAR_WINNER" },
                    prizeAmount = prize,
                    killPoints = kills,
                    placementPoints = place,
                    totalPoints = kills + place
                )
                onPublish(r)
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EsportsGold),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Publish & Reward Winner", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
