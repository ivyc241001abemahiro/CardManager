package com.example.cardmanager.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CardFaceDao {

    @Insert
    suspend fun insert(face: CardFace)

    @Query("SELECT * FROM card_faces WHERE cardId = :cardId ORDER BY faceNo")
    suspend fun getFaces(cardId: Int): List<CardFace>

    @Query("SELECT * FROM card_faces ORDER BY name")
    suspend fun getAll(): List<CardFace>
}