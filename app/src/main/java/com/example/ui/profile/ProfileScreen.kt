package com.example.ui.profile

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.auth.signOut
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
import com.example.ui.tournaments.outlinedTextFieldColors

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onSignedOut: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val myRegistrations by viewModel.myRegistrations.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Text(
                text = "PLAYER PROFILE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = EsportsTextPrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "Manage your Free Fire in-game credentials and account info",
                fontSize = 12.sp,
                color = EsportsTextSecondary
            )
        }

        // Profile Avatar & Main Info Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EsportsBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, EsportsRedPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userProfile?.photoUrl?.isNotBlank() == true) {
                            AsyncImage(
                                model = userProfile!!.photoUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_samar_logo),
                                contentDescription = "Default Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = userProfile?.inGameName?.ifBlank { userProfile?.name } ?: "Free Fire Player",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EsportsTextPrimary
                        )
                        Text(
                            text = userProfile?.email ?: "",
                            fontSize = 12.sp,
                            color = EsportsTextSecondary
                        )

                        if (isAdmin) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2E1015))
                                    .border(1.dp, EsportsRedPrimary, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "PLATFORM ADMINISTRATOR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EsportsRedPrimary
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { showEditProfileDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EsportsSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = EsportsTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Game Credentials", color = EsportsTextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Credentials Grid (UID, IGN, Mobile)
        item {
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "LINKED GAME DATA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EsportsRedPrimary,
                        letterSpacing = 1.sp
                    )

                    ProfileItemRow(
                        icon = Icons.Default.Badge,
                        label = "Free Fire Player UID",
                        value = userProfile?.freeFireUid?.ifBlank { "Not Set (Tap Edit)" } ?: "Not Set"
                    )
                    ProfileItemRow(
                        icon = Icons.Default.SportsEsports,
                        label = "In-Game Name (IGN)",
                        value = userProfile?.inGameName?.ifBlank { "Not Set" } ?: "Not Set"
                    )
                    ProfileItemRow(
                        icon = Icons.Default.Phone,
                        label = "Mobile Number",
                        value = userProfile?.mobileNumber?.ifBlank { "Not Linked" } ?: "Not Linked"
                    )
                }
            }
        }

        // Stats Card
        item {
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
                    Text(
                        text = "CAREER PERFORMANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EsportsGold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "MATCHES", fontSize = 10.sp, color = EsportsTextSecondary)
                            Text(
                                text = "${myRegistrations.size}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = EsportsTextPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "EARNED", fontSize = 10.sp, color = EsportsTextSecondary)
                            Text(
                                text = "${wallet?.totalEarned ?: 0} W",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00E676)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "WITHDRAWN", fontSize = 10.sp, color = EsportsTextSecondary)
                            Text(
                                text = "${wallet?.totalWithdrawn ?: 0} W",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = EsportsGold
                            )
                        }
                    }
                }
            }
        }

        // Admin Panel Shortcut (accessible to all during review or admin accounts)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, EsportsRedPrimary.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.navigateTo(Screen.Admin) }
                    .testTag("open_admin_dashboard_button"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF220E13)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EsportsRedPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Admin Control Panel",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Manage Tournaments, Rooms, Coins & Withdrawals",
                                fontSize = 11.sp,
                                color = EsportsTextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = EsportsRedPrimary
                    )
                }
            }
        }

        // Sign Out Button
        item {
            Button(
                onClick = {
                    signOut(
                        context = context,
                        credentialManager = credentialManager,
                        onSignOutComplete = onSignedOut,
                        scope = coroutineScope
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sign_out_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A1519)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = EsportsRedPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Out",
                    color = EsportsRedPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Edit Profile Modal
    if (showEditProfileDialog) {
        var nameText by remember { mutableStateOf(userProfile?.name ?: "") }
        var ffUidText by remember { mutableStateOf(userProfile?.freeFireUid ?: "") }
        var ignText by remember { mutableStateOf(userProfile?.inGameName ?: "") }
        var mobileText by remember { mutableStateOf(userProfile?.mobileNumber ?: "") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = EsportsSurface,
            title = {
                Text(
                    text = "Edit Profile & Free Fire UID",
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
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_display_name_input"),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = ffUidText,
                        onValueChange = { ffUidText = it },
                        label = { Text("Free Fire Player UID") },
                        placeholder = { Text("e.g. 192837465") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_player_uid_input"),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = ignText,
                        onValueChange = { ignText = it },
                        label = { Text("In-Game Name (IGN)") },
                        placeholder = { Text("e.g. SAMAR_LETHAL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_ign_input"),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = mobileText,
                        onValueChange = { mobileText = it },
                        label = { Text("Mobile Number (For Payouts)") },
                        placeholder = { Text("e.g. +91 9876543210") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_mobile_input"),
                        colors = outlinedTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(
                            name = nameText.trim(),
                            mobile = mobileText.trim(),
                            ffUid = ffUidText.trim(),
                            ign = ignText.trim(),
                            onSuccess = { showEditProfileDialog = false }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EsportsRedPrimary),
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = EsportsTextSecondary)
                }
            }
        )
    }
}

@Composable
fun ProfileItemRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EsportsSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = EsportsTextSecondary, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(text = label, fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EsportsTextPrimary)
        }
    }
}
