package com.example.cardmanager.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DeckDao {

    @Insert
    suspend fun insert(deck: Deck)

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Query("SELECT * FROM decks")
    suspend fun getAll(): List<Deck>

    @Query("SELECT * FROM decks WHERE name LIKE '%' || :keyword || '%'")
    suspend fun search(keyword: String): List<Deck>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getDeck(id: Int): Deck
}