package com.example.ui.wallet

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Transaction
import com.example.data.model.Withdrawal
import com.example.ui.MainViewModel
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsGoldLight
import com.example.ui.theme.EsportsRedGlow
import com.example.ui.theme.EsportsRedPrimary
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceCard
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextMuted
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary
import com.example.ui.tournaments.outlinedTextFieldColors
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun WalletScreen(
    viewModel: MainViewModel
) {
    val wallet by viewModel.wallet.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()

    var showAddCoinsDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var selectedWalletTab by remember { mutableStateOf(0) }

    val coinBalance = wallet?.coinBalance ?: 0L
    val totalEarned = wallet?.totalEarned ?: 0L
    val totalWithdrawn = wallet?.totalWithdrawn ?: 0L

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Title
        item {
            Text(
                text = "WIDARA COIN WALLET",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = EsportsTextPrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "Official currency for Free Fire match entries & winnings",
                fontSize = 12.sp,
                color = EsportsTextSecondary
            )
        }

        // 2. Main Balance Card (Gold + Red Neon Gradient)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, EsportsGold.copy(alpha = 0.7f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF191308)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF2E2007),
                                    Color(0xFF140E05)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AVAILABLE BALANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = EsportsGold,
                                letterSpacing = 1.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = EsportsGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VERIFIED VAULT",
                                    fontSize = 10.sp,
                                    color = EsportsGoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Big Coin Balance
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(EsportsGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "W",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "$coinBalance",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "COINS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EsportsGoldLight,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }

                        // Lifetime Stats: Total Earned & Total Withdrawn
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "TOTAL WON", fontSize = 10.sp, color = EsportsTextSecondary)
                                Text(
                                    text = "$totalEarned Coins",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(26.dp)
                                    .background(Color.White.copy(alpha = 0.1f))
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "CASHED OUT", fontSize = 10.sp, color = EsportsTextSecondary)
                                Text(
                                    text = "$totalWithdrawn Coins",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EsportsGoldLight
                                )
                            }
                        }

                        // Action Buttons: Add Coins & Withdraw
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showAddCoinsDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("add_coins_main_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = EsportsGold),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Add Coins",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { showWithdrawDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("withdraw_coins_main_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = EsportsRedPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Withdraw",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Tab Switcher: "TRANSACTIONS" & "WITHDRAWAL REQUESTS"
        item {
            TabRow(
                selectedTabIndex = selectedWalletTab,
                containerColor = EsportsSurfaceCard,
                contentColor = EsportsRedPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedWalletTab]),
                        color = EsportsRedPrimary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedWalletTab == 0,
                    onClick = { selectedWalletTab = 0 },
                    text = {
                        Text(
                            text = "TRANSACTIONS (${transactions.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedWalletTab == 0) EsportsRedPrimary else EsportsTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedWalletTab == 1,
                    onClick = { selectedWalletTab = 1 },
                    text = {
                        Text(
                            text = "WITHDRAWALS (${withdrawals.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedWalletTab == 1) EsportsRedPrimary else EsportsTextSecondary
                        )
                    }
                )
            }
        }

        // 4. Tab 0: Transactions List
        if (selectedWalletTab == 0) {
            if (transactions.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.Payments,
                        title = "No Transactions Recorded",
                        description = "Your coin credits, match entries and payouts will appear here."
                    )
                }
            } else {
                items(transactions) { tx ->
                    TransactionItem(tx = tx)
                }
            }
        } else {
            // Tab 1: Withdrawal Requests List
            if (withdrawals.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.AccountBalance,
                        title = "No Withdrawal Requests",
                        description = "Cashing out Widara Coins via UPI will display status and audit notes here."
                    )
                }
            } else {
                items(withdrawals) { w ->
                    WithdrawalItem(w = w)
                }
            }
        }
    }

    // Add Coins Dialog
    if (showAddCoinsDialog) {
        val coinPacks = listOf(
            Pair(50L, "₹50 (Standard Pack)"),
            Pair(100L, "₹100 (Popular Pack + Bonus)"),
            Pair(250L, "₹250 (Warrior Pack)"),
            Pair(500L, "₹500 (Pro Champion Pack)"),
            Pair(1000L, "₹1000 (Elite Master Pack)")
        )

        AlertDialog(
            onDismissRequest = { showAddCoinsDialog = false },
            containerColor = EsportsSurface,
            title = {
                Text(
                    text = "Add Widara Coins",
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
                        text = "Select a Widara Coin package to top up your wallet instantly:",
                        fontSize = 12.sp,
                        color = EsportsTextSecondary
                    )

                    coinPacks.forEach { (coins, label) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, EsportsBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.addCoins(coins, label)
                                    showAddCoinsDialog = false
                                },
                            colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(EsportsGold),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "W", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                    }
                                    Column {
                                        Text(text = "$coins Coins", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EsportsTextPrimary)
                                        Text(text = label, fontSize = 11.sp, color = EsportsTextSecondary)
                                    }
                                }
                                Button(
                                    onClick = {
                                        viewModel.addCoins(coins, label)
                                        showAddCoinsDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EsportsGold),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Add", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddCoinsDialog = false }) {
                    Text("Close", color = EsportsTextSecondary)
                }
            }
        )
    }

    // Withdraw Coins Dialog
    if (showWithdrawDialog) {
        var withdrawAmountText by remember { mutableStateOf("100") }
        var upiIdText by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = EsportsSurface,
            title = {
                Text(
                    text = "Withdraw Widara Coins",
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
                    Text(
                        text = "Coins are converted 1 Coin = ₹1 INR. Minimum withdrawal is 50 Coins. Subject to Admin verification.",
                        fontSize = 11.sp,
                        color = EsportsTextSecondary,
                        lineHeight = 15.sp
                    )

                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = {
                            withdrawAmountText = it.filter { char -> char.isDigit() }
                        },
                        label = { Text("Coins to Withdraw") },
                        placeholder = { Text("e.g. 100") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input"),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = upiIdText,
                        onValueChange = { upiIdText = it },
                        label = { Text("UPI ID or Bank Account") },
                        placeholder = { Text("e.g. yourname@oksbi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("withdraw_upi_input"),
                        colors = outlinedTextFieldColors()
                    )

                    errorMsg?.let {
                        Text(text = it, color = EsportsRedPrimary, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmountText.toLongOrNull() ?: 0L
                        if (amount < 50) {
                            errorMsg = "Minimum withdrawal is 50 coins."
                            return@Button
                        }
                        if (amount > coinBalance) {
                            errorMsg = "You only have $coinBalance coins available."
                            return@Button
                        }
                        if (upiIdText.isBlank()) {
                            errorMsg = "Please enter your valid UPI ID."
                            return@Button
                        }

                        viewModel.requestWithdrawal(
                            amount = amount,
                            upiId = upiIdText.trim(),
                            onSuccess = {
                                showWithdrawDialog = false
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EsportsRedPrimary),
                    modifier = Modifier.testTag("submit_withdraw_button")
                ) {
                    Text("Submit Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = EsportsTextSecondary)
                }
            }
        )
    }
}

@Composable
fun TransactionItem(tx: Transaction) {
    val isCredit = tx.type == "CREDIT"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EsportsBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCredit) Color(0xFF0F2618) else Color(0xFF291017)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (isCredit) Color(0xFF00E676) else EsportsRedPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = tx.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EsportsTextPrimary
                    )
                    Text(
                        text = tx.description.ifBlank { tx.category },
                        fontSize = 11.sp,
                        color = EsportsTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}${tx.amount} Coins",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isCredit) Color(0xFF00E676) else EsportsRedPrimary
                )
                Text(
                    text = tx.status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (tx.status) {
                        "SUCCESS" -> Color(0xFF00E676)
                        "PENDING" -> EsportsGold
                        else -> EsportsRedPrimary
                    }
                )
            }
        }
    }
}

@Composable
fun WithdrawalItem(w: Withdrawal) {
    val statusColor = when (w.status) {
        "APPROVED" -> Color(0xFF00E676)
        "PENDING" -> EsportsGold
        else -> EsportsRedPrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EsportsBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (w.status) {
                            "APPROVED" -> Icons.Default.CheckCircle
                            "PENDING" -> Icons.Default.HourglassTop
                            else -> Icons.Default.Error
                        },
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STATUS: ${w.status}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor
                    )
                }

                Text(
                    text = "-${w.amount} Coins",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = EsportsGold
                )
            }

            Text(
                text = "Payout Destination: ${w.upiIdOrBank}",
                fontSize = 12.sp,
                color = EsportsTextPrimary,
                fontWeight = FontWeight.Medium
            )

            if (w.adminNote.isNotBlank()) {
                Text(
                    text = "Admin Note: ${w.adminNote}",
                    fontSize = 11.sp,
                    color = EsportsTextSecondary
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EsportsTextMuted,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EsportsTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = EsportsTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
