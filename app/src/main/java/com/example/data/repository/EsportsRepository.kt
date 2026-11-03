package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.TournamentDao
import com.example.data.local.TournamentEntity
import com.example.data.model.Registration
import com.example.data.model.ResultRecord
import com.example.data.model.Tournament
import com.example.data.model.Transaction
import com.example.data.model.UserProfile
import com.example.data.model.Wallet
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EsportsRepository(
    val db: FirebaseFirestore,
    val auth: FirebaseAuth = Firebase.auth,
    val localTournamentDao: TournamentDao? = null
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth,
        AppDatabase.getDatabase(context.applicationContext).tournamentDao()
    )

    val adminEmail: String = "laljikush70@gmail.com"

    fun currentUserId(): String? = auth.currentUser?.uid
    fun currentUserEmail(): String? = auth.currentUser?.email

    fun isCurrentUserAdmin(): Boolean {
        val email = currentUserEmail()
        return email != null && email.equals(adminEmail, ignoreCase = true)
    }

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing tournament data.")
    }

    // -------------------------------------------------------------
    // USER PROFILE & WALLET INITIALIZATION
    // -------------------------------------------------------------

    fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val docRef = db.collection("users").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, docRef.path)
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(UserProfile::class.java))
        }
        awaitClose { listener.remove() }
    }

    suspend fun ensureUserAndWallet(user: FirebaseUser): UserProfile {
        val uid = user.uid
        val userDoc = db.collection("users").document(uid).get().await()
        val existingProfile = userDoc.toObject(UserProfile::class.java)

        val profile = if (existingProfile != null) {
            existingProfile
        } else {
            val isAdminUser = user.email?.equals(adminEmail, ignoreCase = true) == true
            val newProfile = UserProfile(
                userId = uid,
                email = user.email ?: "",
                name = user.displayName ?: "FreeFire Player",
                mobileNumber = "",
                freeFireUid = "FF" + (10000000..99999999).random(),
                inGameName = (user.displayName ?: "Warrior").filter { it.isLetterOrDigit() }.take(15),
                photoUrl = user.photoUrl?.toString() ?: "",
                role = if (isAdminUser) "admin" else "player"
            )
            val payload = hashMapOf<String, Any>(
                "userId" to newProfile.userId,
                "email" to newProfile.email,
                "name" to newProfile.name,
                "mobileNumber" to newProfile.mobileNumber,
                "freeFireUid" to newProfile.freeFireUid,
                "inGameName" to newProfile.inGameName,
                "photoUrl" to newProfile.photoUrl,
                "role" to newProfile.role,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(uid).set(payload).await()
            newProfile
        }

        // Ensure wallet exists
        val walletDoc = db.collection("wallets").document(uid).get().await()
        if (!walletDoc.exists()) {
            val initialWallet = hashMapOf<String, Any>(
                "userId" to uid,
                "coinBalance" to 100L, // Welcome bonus of 100 Widara Coins for new players
                "totalEarned" to 100L,
                "totalWithdrawn" to 0L,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("wallets").document(uid).set(initialWallet).await()

            // Welcome transaction
            val txId = UUID.randomUUID().toString()
            val welcomeTx = hashMapOf<String, Any>(
                "id" to txId,
                "userId" to uid,
                "type" to "CREDIT",
                "category" to "ADMIN_ADJUSTMENT",
                "amount" to 100L,
                "title" to "Welcome Bonus",
                "description" to "100 Widara Coins credited to your SAMAR ESPORTS wallet!",
                "status" to "SUCCESS",
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("transactions").document(txId).set(welcomeTx).await()
        }

        return profile
    }

    suspend fun updateProfile(
        name: String,
        mobileNumber: String,
        freeFireUid: String,
        inGameName: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val updates = hashMapOf<String, Any>(
            "name" to name.trim(),
            "mobileNumber" to mobileNumber.trim(),
            "freeFireUid" to freeFireUid.trim(),
            "inGameName" to inGameName.trim(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.update(updates).await()
    }

    // -------------------------------------------------------------
    // TOURNAMENTS
    // -------------------------------------------------------------

    fun observeTournaments(): Flow<List<Tournament>> = callbackFlow {
        val query = db.collection("tournaments").orderBy("createdAt", Query.Direction.DESCENDING)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, "tournaments")
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(Tournament::class.java) ?: emptyList()
            trySend(items)

            // Cache to local Room database for offline persistence
            if (items.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        localTournamentDao?.insertTournaments(
                            items.map { TournamentEntity.fromDomainModel(it) }
                        )
                    } catch (e: Exception) {
                        // Non-blocking local cache
                    }
                }
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Reads tournaments directly from the local Room database (for offline use).
     */
    fun observeLocalTournaments(): Flow<List<TournamentEntity>>? {
        return localTournamentDao?.getAllTournaments()
    }


    suspend fun createTournament(tournament: Tournament): Result<String> = runCatching {
        requireUserId()
        val docId = if (tournament.id.isNotBlank()) tournament.id else UUID.randomUUID().toString()
        val payload = hashMapOf<String, Any>(
            "id" to docId,
            "name" to tournament.name.trim(),
            "category" to tournament.category,
            "matchDateTime" to tournament.matchDateTime,
            "entryFee" to tournament.entryFee,
            "prizePool" to tournament.prizePool,
            "maxSlots" to tournament.maxSlots,
            "joinedSlots" to 0L,
            "rules" to tournament.rules,
            "roomId" to tournament.roomId,
            "roomPassword" to tournament.roomPassword,
            "status" to tournament.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("tournaments").document(docId).set(payload).await()
        docId
    }

    suspend fun updateTournamentRoom(
        tournamentId: String,
        roomId: String,
        roomPassword: String
    ): Result<Unit> = runCatching {
        requireUserId()
        db.collection("tournaments").document(tournamentId).update(
            mapOf(
                "roomId" to roomId.trim(),
                "roomPassword" to roomPassword.trim(),
                "status" to "ONGOING",
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun updateTournamentStatus(
        tournamentId: String,
        status: String
    ): Result<Unit> = runCatching {
        requireUserId()
        db.collection("tournaments").document(tournamentId).update(
            mapOf(
                "status" to status,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun deleteTournament(tournamentId: String): Result<Unit> = runCatching {
        requireUserId()
        db.collection("tournaments").document(tournamentId).delete().await()
    }

    // -------------------------------------------------------------
    // JOIN MATCH & REGISTRATION
    // -------------------------------------------------------------

    fun observeUserRegistrations(userId: String): Flow<List<Registration>> = callbackFlow {
        val query = db.collection("registrations").whereEqualTo("userId", userId)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, "registrations")
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(Registration::class.java) ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    suspend fun joinTournament(
        tournament: Tournament,
        playerUid: String,
        inGameName: String,
        userName: String,
        teammates: List<String> = emptyList()
    ): Result<String> = runCatching {
        val uid = requireUserId()

        // 1. Check if user already registered for this tournament
        val existingRegs = db.collection("registrations")
            .whereEqualTo("tournamentId", tournament.id)
            .whereEqualTo("userId", uid)
            .get().await()

        if (!existingRegs.isEmpty) {
            throw IllegalStateException("You are already registered for this tournament!")
        }

        // 2. Fetch current wallet balance
        val walletDoc = db.collection("wallets").document(uid).get().await()
        val currentBalance = walletDoc.getLong("coinBalance") ?: 0L

        if (currentBalance < tournament.entryFee) {
            throw IllegalStateException("Insufficient Widara Coins! Entry fee: ${tournament.entryFee} coins. Your balance: $currentBalance coins.")
        }

        if (tournament.joinedSlots >= tournament.maxSlots) {
            throw IllegalStateException("This tournament is already full (${tournament.maxSlots}/${tournament.maxSlots} slots occupied).")
        }

        // 3. Deduct entry fee & update wallet
        val newBalance = currentBalance - tournament.entryFee
        db.collection("wallets").document(uid).update(
            mapOf(
                "coinBalance" to newBalance,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        // 4. Create Transaction record
        val txId = UUID.randomUUID().toString()
        val txPayload = hashMapOf<String, Any>(
            "id" to txId,
            "userId" to uid,
            "type" to "DEBIT",
            "category" to "TOURNAMENT_ENTRY",
            "amount" to tournament.entryFee,
            "title" to "Entry: ${tournament.name}",
            "description" to "Joined Free Fire ${tournament.category} tournament (Slot reserved)",
            "status" to "SUCCESS",
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("transactions").document(txId).set(txPayload).await()

        // 5. Create Registration record
        val regId = UUID.randomUUID().toString()
        val regPayload = hashMapOf<String, Any>(
            "id" to regId,
            "tournamentId" to tournament.id,
            "userId" to uid,
            "userName" to userName,
            "playerUid" to playerUid,
            "inGameName" to inGameName,
            "category" to tournament.category,
            "teammates" to teammates,
            "entryFeePaid" to tournament.entryFee,
            "status" to "REGISTERED",
            "joinedAt" to FieldValue.serverTimestamp()
        )
        db.collection("registrations").document(regId).set(regPayload).await()

        // 6. Increment tournament joinedSlots
        db.collection("tournaments").document(tournament.id).update(
            mapOf(
                "joinedSlots" to FieldValue.increment(1),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        regId
    }

    // -------------------------------------------------------------
    // WALLET & TRANSACTIONS
    // -------------------------------------------------------------

    fun observeWallet(userId: String): Flow<Wallet?> = callbackFlow {
        val docRef = db.collection("wallets").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, docRef.path)
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(Wallet::class.java))
        }
        awaitClose { listener.remove() }
    }

    fun observeUserTransactions(userId: String): Flow<List<Transaction>> = callbackFlow {
        val query = db.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, "transactions")
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(Transaction::class.java) ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    suspend fun addCoins(
        amount: Long,
        title: String = "Purchased Widara Coins",
        description: String = "Coin pack added to wallet"
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val walletDoc = db.collection("wallets").document(uid).get().await()
        val curBalance = walletDoc.getLong("coinBalance") ?: 0L

        db.collection("wallets").document(uid).update(
            mapOf(
                "coinBalance" to (curBalance + amount),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        val txId = UUID.randomUUID().toString()
        val txPayload = hashMapOf<String, Any>(
            "id" to txId,
            "userId" to uid,
            "type" to "CREDIT",
            "category" to "COIN_PURCHASE",
            "amount" to amount,
            "title" to title,
            "description" to description,
            "status" to "SUCCESS",
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("transactions").document(txId).set(txPayload).await()
    }

    // -------------------------------------------------------------
    // WITHDRAWALS
    // -------------------------------------------------------------

    fun observeUserWithdrawals(userId: String): Flow<List<com.example.data.model.Withdrawal>> = callbackFlow {
        val query = db.collection("withdrawals")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, "withdrawals")
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(com.example.data.model.Withdrawal::class.java) ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    suspend fun requestWithdrawal(
        userName: String,
        amount: Long,
        upiIdOrBank: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        if (amount < 50) {
            throw IllegalArgumentException("Minimum withdrawal is 50 Widara Coins.")
        }
        val walletDoc = db.collection("wallets").document(uid).get().await()
        val curBalance = walletDoc.getLong("coinBalance") ?: 0L
        if (curBalance < amount) {
            throw IllegalStateException("Insufficient Widara Coins for withdrawal. Balance: $curBalance")
        }

        // Deduct coins pending admin review
        db.collection("wallets").document(uid).update(
            mapOf(
                "coinBalance" to (curBalance - amount),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        val withdrawalId = UUID.randomUUID().toString()
        val payload = hashMapOf<String, Any>(
            "id" to withdrawalId,
            "userId" to uid,
            "userName" to userName,
            "amount" to amount,
            "upiIdOrBank" to upiIdOrBank.trim(),
            "status" to "PENDING",
            "adminNote" to "Under review by admin",
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("withdrawals").document(withdrawalId).set(payload).await()

        val txId = UUID.randomUUID().toString()
        val txPayload = hashMapOf<String, Any>(
            "id" to txId,
            "userId" to uid,
            "type" to "DEBIT",
            "category" to "WITHDRAWAL",
            "amount" to amount,
            "title" to "Withdrawal Request ($amount Coins)",
            "description" to "Payout to $upiIdOrBank (Status: PENDING approval)",
            "status" to "PENDING",
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("transactions").document(txId).set(txPayload).await()

        withdrawalId
    }

    // -------------------------------------------------------------
    // RESULTS & LIVE LEADERBOARDS
    // -------------------------------------------------------------

    fun observeResults(): Flow<List<ResultRecord>> = callbackFlow {
        val query = db.collection("results").orderBy("completedAt", Query.Direction.DESCENDING)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, "results")
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(ResultRecord::class.java) ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    // -------------------------------------------------------------
    // ADMIN FUNCTIONS
    // -------------------------------------------------------------

    fun observeAllWithdrawals(): Flow<List<com.example.data.model.Withdrawal>> = callbackFlow {
        val query = db.collection("withdrawals").orderBy("createdAt", Query.Direction.DESCENDING)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, "withdrawals")
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(com.example.data.model.Withdrawal::class.java) ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    suspend fun adminProcessWithdrawal(
        withdrawal: com.example.data.model.Withdrawal,
        approve: Boolean,
        adminNote: String
    ): Result<Unit> = runCatching {
        requireUserId()
        val newStatus = if (approve) "APPROVED" else "REJECTED"
        db.collection("withdrawals").document(withdrawal.id).update(
            mapOf(
                "status" to newStatus,
                "adminNote" to adminNote,
                "processedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        if (!approve) {
            // Refund coins to user wallet
            val walletDoc = db.collection("wallets").document(withdrawal.userId).get().await()
            val curBalance = walletDoc.getLong("coinBalance") ?: 0L
            db.collection("wallets").document(withdrawal.userId).update(
                mapOf(
                    "coinBalance" to (curBalance + withdrawal.amount),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // Refund transaction
            val txId = UUID.randomUUID().toString()
            val txPayload = hashMapOf<String, Any>(
                "id" to txId,
                "userId" to withdrawal.userId,
                "type" to "CREDIT",
                "category" to "ADMIN_ADJUSTMENT",
                "amount" to withdrawal.amount,
                "title" to "Withdrawal Rejected (Refunded)",
                "description" to "Reason: $adminNote",
                "status" to "SUCCESS",
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("transactions").document(txId).set(txPayload).await()
        } else {
            // Update user wallet totalWithdrawn
            db.collection("wallets").document(withdrawal.userId).update(
                mapOf(
                    "totalWithdrawn" to FieldValue.increment(withdrawal.amount),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun adminAdjustCoins(
        targetUserId: String,
        amountDelta: Long,
        isAdd: Boolean,
        reason: String
    ): Result<Unit> = runCatching {
        requireUserId()
        val walletDoc = db.collection("wallets").document(targetUserId).get().await()
        val curBalance = walletDoc.getLong("coinBalance") ?: 0L
        val newBalance = if (isAdd) (curBalance + amountDelta) else maxOf(0L, curBalance - amountDelta)

        db.collection("wallets").document(targetUserId).update(
            mapOf(
                "coinBalance" to newBalance,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        val txId = UUID.randomUUID().toString()
        val txPayload = hashMapOf<String, Any>(
            "id" to txId,
            "userId" to targetUserId,
            "type" to if (isAdd) "CREDIT" else "DEBIT",
            "category" to "ADMIN_ADJUSTMENT",
            "amount" to amountDelta,
            "title" to if (isAdd) "Admin Coin Credit" else "Admin Coin Deduction",
            "description" to reason,
            "status" to "SUCCESS",
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("transactions").document(txId).set(txPayload).await()
    }

    suspend fun adminPublishResult(
        result: ResultRecord
    ): Result<Unit> = runCatching {
        requireUserId()
        val resultId = if (result.id.isNotBlank()) result.id else UUID.randomUUID().toString()
        val payload = hashMapOf<String, Any>(
            "id" to resultId,
            "tournamentId" to result.tournamentId,
            "tournamentName" to result.tournamentName,
            "winnerUid" to result.winnerUid,
            "winnerName" to result.winnerName,
            "winnerInGameName" to result.winnerInGameName,
            "prizeAmount" to result.prizeAmount,
            "killPoints" to result.killPoints,
            "placementPoints" to result.placementPoints,
            "totalPoints" to result.totalPoints,
            "completedAt" to FieldValue.serverTimestamp()
        )
        db.collection("results").document(resultId).set(payload).await()

        // Set tournament to COMPLETED
        db.collection("tournaments").document(result.tournamentId).update(
            mapOf(
                "status" to "COMPLETED",
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        // Reward winner with prize coins
        if (result.winnerUid.isNotBlank() && result.prizeAmount > 0) {
            val walletDoc = db.collection("wallets").document(result.winnerUid).get().await()
            if (walletDoc.exists()) {
                val curBalance = walletDoc.getLong("coinBalance") ?: 0L
                val curEarned = walletDoc.getLong("totalEarned") ?: 0L
                db.collection("wallets").document(result.winnerUid).update(
                    mapOf(
                        "coinBalance" to (curBalance + result.prizeAmount),
                        "totalEarned" to (curEarned + result.prizeAmount),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()

                val txId = UUID.randomUUID().toString()
                val txPayload = hashMapOf<String, Any>(
                    "id" to txId,
                    "userId" to result.winnerUid,
                    "type" to "CREDIT",
                    "category" to "TOURNAMENT_PRIZE",
                    "amount" to result.prizeAmount,
                    "title" to "Prize: ${result.tournamentName}",
                    "description" to "1st Place Winner reward (${result.prizeAmount} Widara Coins)",
                    "status" to "SUCCESS",
                    "createdAt" to FieldValue.serverTimestamp()
                )
                db.collection("transactions").document(txId).set(txPayload).await()
            }
        }
    }

    suspend fun seedSampleDataIfEmpty() {
        try {
            val tournamentsSnap = db.collection("tournaments").limit(1).get().await()
            if (tournamentsSnap.isEmpty) {
                // Add initial tournaments
                val t1 = Tournament(
                    id = "tourney_solo_01",
                    name = "Free Fire Pro Solo Clash",
                    category = "SOLO",
                    matchDateTime = "Today, 08:00 PM IST",
                    entryFee = 30L,
                    prizePool = 1200L,
                    maxSlots = 48L,
                    joinedSlots = 28L,
                    rules = "1. Classic Bermuda Map.\n2. Emulator / iPad strictly prohibited.\n3. Room ID & Password shared 15 mins before match.",
                    status = "UPCOMING"
                )
                createTournament(t1)

                val t2 = Tournament(
                    id = "tourney_duo_01",
                    name = "Bermuda Duo Showdown",
                    category = "DUO",
                    matchDateTime = "Tomorrow, 06:30 PM IST",
                    entryFee = 50L,
                    prizePool = 2400L,
                    maxSlots = 24L,
                    joinedSlots = 14L,
                    rules = "1. Purgatory Map.\n2. Both teammates must join with verified In-Game UIDs.\n3. Per Kill: 10 Coins, Winner: 1000 Coins.",
                    status = "UPCOMING"
                )
                createTournament(t2)

                val t3 = Tournament(
                    id = "tourney_squad_01",
                    name = "India Squad Championship",
                    category = "SQUAD",
                    matchDateTime = "Sunday, 09:00 PM IST",
                    entryFee = 100L,
                    prizePool = 5000L,
                    maxSlots = 12L,
                    joinedSlots = 8L,
                    rules = "1. Kalahari Map.\n2. Squad leader must register all 4 players.\n3. Live streaming with anti-cheat referee.",
                    status = "UPCOMING"
                )
                createTournament(t3)

                // Add sample completed result for leaderboard
                val r1 = ResultRecord(
                    id = "result_01",
                    tournamentId = "tourney_solo_past",
                    tournamentName = "Free Fire Masters Season 1",
                    winnerUid = "winner_pro_01",
                    winnerName = "Aman Sharma",
                    winnerInGameName = "SAMAR_GODFATHER",
                    prizeAmount = 2500L,
                    killPoints = 140L,
                    placementPoints = 200L,
                    totalPoints = 340L
                )
                val payload = hashMapOf<String, Any>(
                    "id" to r1.id,
                    "tournamentId" to r1.tournamentId,
                    "tournamentName" to r1.tournamentName,
                    "winnerUid" to r1.winnerUid,
                    "winnerName" to r1.winnerName,
                    "winnerInGameName" to r1.winnerInGameName,
                    "prizeAmount" to r1.prizeAmount,
                    "killPoints" to r1.killPoints,
                    "placementPoints" to r1.placementPoints,
                    "totalPoints" to r1.totalPoints,
                    "completedAt" to FieldValue.serverTimestamp()
                )
                db.collection("results").document(r1.id).set(payload).await()
            }
        } catch (e: Exception) {
            // Ignore seeding errors
        }
    }
}
