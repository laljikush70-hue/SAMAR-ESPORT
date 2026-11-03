package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Room local tournament operations.
 */
@Dao
interface TournamentDao {
    @Query("SELECT * FROM tournaments ORDER BY id DESC")
    fun getAllTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE category = :category ORDER BY id DESC")
    fun getTournamentsByCategory(category: String): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    fun getTournamentById(id: String): Flow<TournamentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournaments(tournaments: List<TournamentEntity>)

    @Query("DELETE FROM tournaments WHERE id = :id")
    suspend fun deleteTournamentById(id: String)

    @Query("DELETE FROM tournaments")
    suspend fun clearAll()
}
