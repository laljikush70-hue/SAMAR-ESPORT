package com.example.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResultRecord
import com.example.ui.MainViewModel
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsGoldLight
import com.example.ui.theme.EsportsRedGlow
import com.example.ui.theme.EsportsRedPrimary
import com.example.ui.theme.EsportsSurfaceCard
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextMuted
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun LeaderboardScreen(
    viewModel: MainViewModel
) {
    val results by viewModel.results.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "LIVE LEADERBOARDS & WINNERS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = EsportsTextPrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "Hall of Fame, Champions, Kill Points & Official Match Outcomes",
                fontSize = 12.sp,
                color = EsportsTextSecondary
            )
        }

        if (results.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = EsportsTextMuted,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Leaderboard Updating",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EsportsTextPrimary
                        )
                        Text(
                            text = "Official match scores and winner rewards will appear here after each match concludes.",
                            fontSize = 12.sp,
                            color = EsportsTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Podium / Champion Top Card
            item {
                val topWinner = results.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, EsportsGold, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1609)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(EsportsGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Text(
                            text = "#1 TOURNAMENT CHAMPION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = EsportsGold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = topWinner.winnerInGameName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Text(
                            text = "${topWinner.winnerName} • ${topWinner.tournamentName}",
                            fontSize = 12.sp,
                            color = EsportsGoldLight
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.4f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "KILL POINTS", fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
                                Text(text = "${topWinner.killPoints}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EsportsRedPrimary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "PLACEMENT", fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
                                Text(text = "${topWinner.placementPoints}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "TOTAL POINTS", fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
                                Text(text = "${topWinner.totalPoints}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "PRIZE WON", fontSize = 10.sp, color = EsportsTextMuted, fontWeight = FontWeight.Bold)
                                Text(text = "${topWinner.prizeAmount} Coins", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EsportsGold)
                            }
                        }
                    }
                }
            }

            // Results List
            itemsIndexed(results) { index, result ->
                LeaderboardResultCard(rank = index + 1, result = result)
            }
        }
    }
}

@Composable
fun LeaderboardResultCard(
    rank: Int,
    result: ResultRecord
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EsportsBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = EsportsSurfaceCard),
        shape = RoundedCornerShape(14.dp)
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
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            when (rank) {
                                1 -> EsportsGold
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> EsportsSurfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (rank <= 3) Color.Black else Color.White
                    )
                }

                Column {
                    Text(
                        text = result.winnerInGameName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EsportsTextPrimary
                    )
                    Text(
                        text = "${result.winnerName} • ${result.tournamentName}",
                        fontSize = 11.sp,
                        color = EsportsTextSecondary
                    )
                    Text(
                        text = "Kills: ${result.killPoints} | Placement: ${result.placementPoints} | Total: ${result.totalPoints} pts",
                        fontSize = 11.sp,
                        color = EsportsRedGlow,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${result.prizeAmount} Coins",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EsportsGold
                )
                Text(
                    text = "Winner Reward",
                    fontSize = 10.sp,
                    color = EsportsTextSecondary
                )
            }
        }
    }
}
