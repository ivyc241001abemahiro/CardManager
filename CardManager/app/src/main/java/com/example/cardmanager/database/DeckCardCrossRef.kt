package com.example.cardmanager.database

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "deck_card_cross_ref",
    primaryKeys = ["deckId", "variantId", "zoneType"],
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CardVariant::class,
            parentColumns = ["id"],
            childColumns = ["variantId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DeckCardCrossRef(
    val deckId: Int,
    val variantId: Int,
    val count: Int,
    val zoneType: String
)
