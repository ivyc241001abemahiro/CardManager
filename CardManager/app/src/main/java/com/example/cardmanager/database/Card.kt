package com.example.cardmanager.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val civilization: String,
    val maxCount: Int = 4,
    val requiredCount: Int = 0
)
