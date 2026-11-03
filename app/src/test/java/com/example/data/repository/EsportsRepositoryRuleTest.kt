package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Tournament
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class EsportsRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun observeTournaments_authenticatedUser_receivesTournaments() = runBlocking {
        signInTestUser("alice_test@samaresports.com")
        val repository = EsportsRepository(firestore, auth)

        val list = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.observeTournaments().first()
        }
        assertNotNull(list)
    }

    @Test
    fun observeTournaments_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = EsportsRepository(firestore, auth)

        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                repository.observeTournaments().first()
            }
            assertTrue("Expected exception when unauthenticated", false)
        } catch (e: Exception) {
            val root = e.unwrapFirestoreException()
            assertTrue(
                "Expected permission denied or cancelled channel",
                root?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED || e is kotlinx.coroutines.CancellationException
            )
        }
    }

    @Test
    fun joinTournament_insufficientBalance_throwsException() = runBlocking {
        val uid = signInTestUser("gamer_test@samaresports.com")
        val repository = EsportsRepository(firestore, auth)

        val tourney = Tournament(
            id = "tourney_" + UUID.randomUUID().toString().take(8),
            name = "Test Clash",
            category = "SOLO",
            matchDateTime = "Tomorrow 8 PM",
            entryFee = 5000L, // Very high entry fee
            prizePool = 10000L,
            maxSlots = 48L,
            joinedSlots = 0L,
            status = "UPCOMING"
        )

        var caught = false
        try {
            repository.joinTournament(
                tournament = tourney,
                playerUid = "FF99999",
                inGameName = "ProPlayer",
                userName = "Test User"
            ).getOrThrow()
        } catch (e: Exception) {
            caught = true
            assertTrue(e.message?.contains("Insufficient") == true || e.message?.contains("Coins") == true)
        }
        assertTrue("Expected insufficient balance error", caught)
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 6000L
    }
}
