package com.example.smartsearchbar

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchQuery(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val queryText: String
)
