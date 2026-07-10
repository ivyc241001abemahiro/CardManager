package com.example.cardmanager.database

import androidx.room.*

@Dao
interface CardDao {

    @Insert
    suspend fun insert(card: Card): Long

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)

    @Query("SELECT * FROM cards")
    suspend fun getAll(): List<Card>
}