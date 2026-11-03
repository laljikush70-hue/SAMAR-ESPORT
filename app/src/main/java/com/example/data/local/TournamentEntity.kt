package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Tournament

/**
 * Room database entity for storing tournament details locally on the device.
 */
@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = "",

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "entry_fee")
    val entryFee: Long = 0L,

    @ColumnInfo(name = "prize_pool")
    val prizePool: Long = 0L,

    @ColumnInfo(name = "start_date")
    val startDate: String = "",

    @ColumnInfo(name = "category")
    val category: String = "SOLO",

    @ColumnInfo(name = "max_slots")
    val maxSlots: Long = 48L,

    @ColumnInfo(name = "joined_slots")
    val joinedSlots: Long = 0L,

    @ColumnInfo(name = "status")
    val status: String = "UPCOMING",

    @ColumnInfo(name = "rules")
    val rules: String = "",

    @ColumnInfo(name = "room_id")
    val roomId: String = "",

    @ColumnInfo(name = "room_password")
    val roomPassword: String = "",

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts the Room entity to the domain/UI Tournament model.
     */
    fun toDomainModel(): Tournament = Tournament(
        id = id,
        name = name,
        category = category,
        matchDateTime = startDate,
        entryFee = entryFee,
        prizePool = prizePool,
        maxSlots = maxSlots,
        joinedSlots = joinedSlots,
        rules = rules,
        roomId = roomId,
        roomPassword = roomPassword,
        status = status
    )

    companion object {
        /**
         * Creates a Room entity from the domain/UI Tournament model.
         */
        fun fromDomainModel(domain: Tournament): TournamentEntity = TournamentEntity(
            id = domain.id,
            name = domain.name,
            entryFee = domain.entryFee,
            prizePool = domain.prizePool,
            startDate = domain.matchDateTime,
            category = domain.category,
            maxSlots = domain.maxSlots,
            joinedSlots = domain.joinedSlots,
            status = domain.status,
            rules = domain.rules,
            roomId = domain.roomId,
            roomPassword = domain.roomPassword
        )
    }
}
