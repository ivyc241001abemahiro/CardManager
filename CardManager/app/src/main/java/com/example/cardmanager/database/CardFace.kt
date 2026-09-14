package com.example.cardmanager.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "card_faces",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CardFace(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId: Int,
    val name: String,
    val cost: Int,
    val power: Int? = null,
    val type: String,
    val civilization: String = ""
)
