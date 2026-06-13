package com.example.smartsearchbar

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchQueryDao {
    @Query("SELECT * FROM search_history ORDER BY id DESC")
    fun getAllQueries(): Flow<List<SearchQuery>>

    @Insert
    suspend fun insertQuery(query: SearchQuery)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteQuery(id: Int)
}
