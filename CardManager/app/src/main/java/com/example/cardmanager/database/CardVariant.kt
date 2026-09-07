package com.example.cardmanager.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "card_variants",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CardVariant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId: Int,
    val imagePath: String? = null,
    val variantName: String,
    val ownedCount: Int = 0
)
