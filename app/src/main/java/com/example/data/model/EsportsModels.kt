package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val mobileNumber: String = "",
    val freeFireUid: String = "",
    val inGameName: String = "",
    val photoUrl: String = "",
    val role: String = "player",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Tournament(
    @DocumentId val id: String = "",
    val name: String = "",
    val category: String = "SOLO", // SOLO, DUO, SQUAD
    val matchDateTime: String = "",
    val entryFee: Long = 0L,
    val prizePool: Long = 0L,
    val maxSlots: Long = 48L,
    val joinedSlots: Long = 0L,
    val rules: String = "1. Emulator not allowed\n2. Hackers will be banned permanently\n3. Screenshot of score table is mandatory",
    val roomId: String = "",
    val roomPassword: String = "",
    val status: String = "UPCOMING", // UPCOMING, ONGOING, COMPLETED, CANCELLED
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Registration(
    @DocumentId val id: String = "",
    val tournamentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val playerUid: String = "",
    val inGameName: String = "",
    val category: String = "SOLO",
    val teammates: List<String> = emptyList(),
    val entryFeePaid: Long = 0L,
    val status: String = "REGISTERED",
    @ServerTimestamp val joinedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Wallet(
    @DocumentId val id: String = "",
    val userId: String = "",
    val coinBalance: Long = 0L,
    val totalEarned: Long = 0L,
    val totalWithdrawn: Long = 0L,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Transaction(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "CREDIT", // CREDIT, DEBIT
    val category: String = "COIN_PURCHASE", // TOURNAMENT_ENTRY, TOURNAMENT_PRIZE, COIN_PURCHASE, WITHDRAWAL, ADMIN_ADJUSTMENT
    val amount: Long = 0L,
    val title: String = "",
    val description: String = "",
    val status: String = "SUCCESS", // SUCCESS, PENDING, FAILED
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Withdrawal(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val amount: Long = 0L,
    val upiIdOrBank: String = "",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val adminNote: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val processedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ResultRecord(
    @DocumentId val id: String = "",
    val tournamentId: String = "",
    val tournamentName: String = "",
    val winnerUid: String = "",
    val winnerName: String = "",
    val winnerInGameName: String = "",
    val prizeAmount: Long = 0L,
    val killPoints: Long = 0L,
    val placementPoints: Long = 0L,
    val totalPoints: Long = 0L,
    @ServerTimestamp val completedAt: Timestamp? = null
)
