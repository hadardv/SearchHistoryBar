package com.example.smartsearchbar

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SearchQuery::class], version = 1, exportSchema = false)
abstract class SearchDatabase : RoomDatabase() {
    abstract fun searchQueryDao(): SearchQueryDao
}
