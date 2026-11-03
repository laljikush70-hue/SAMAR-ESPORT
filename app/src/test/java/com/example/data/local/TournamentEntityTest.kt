package com.example.data.local

import com.example.data.model.Tournament
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TournamentEntityTest {

    @Test
    fun testTournamentEntityCreationAndMapping() {
        val entity = TournamentEntity(
            id = "tourney_101",
            name = "Free Fire Pro Solo Clash",
            entryFee = 30L,
            prizePool = 1200L,
            startDate = "2026-11-10 20:00 IST",
            category = "SOLO",
            maxSlots = 48L,
            joinedSlots = 12L,
            status = "UPCOMING",
            rules = "Classic Bermuda rules"
        )

        assertEquals("tourney_101", entity.id)
        assertEquals("Free Fire Pro Solo Clash", entity.name)
        assertEquals(30L, entity.entryFee)
        assertEquals(1200L, entity.prizePool)
        assertEquals("2026-11-10 20:00 IST", entity.startDate)
        assertEquals("SOLO", entity.category)
        assertEquals(48L, entity.maxSlots)
        assertEquals(12L, entity.joinedSlots)
        assertEquals("UPCOMING", entity.status)

        // Convert to domain model
        val domain = entity.toDomainModel()
        assertEquals(entity.id, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.entryFee, domain.entryFee)
        assertEquals(entity.prizePool, domain.prizePool)
        assertEquals(entity.startDate, domain.matchDateTime)

        // Convert back from domain model
        val backToEntity = TournamentEntity.fromDomainModel(domain)
        assertEquals(entity.id, backToEntity.id)
        assertEquals(entity.name, backToEntity.name)
        assertEquals(entity.entryFee, backToEntity.entryFee)
        assertEquals(entity.prizePool, backToEntity.prizePool)
        assertEquals(entity.startDate, backToEntity.startDate)
    }
}
