package com.example.cardmanager.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckName: String,
    val createDate: String,
    val zoneType: String // 例: "Main", "HyperSpatial"
)
