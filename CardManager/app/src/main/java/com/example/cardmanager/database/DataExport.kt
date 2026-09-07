package com.example.cardmanager.database

import kotlinx.serialization.Serializable

@Serializable
data class CardExport(
    val civilization: String,
    val maxCount: Int = 4,
    val requiredCount: Int = 0,
    val faces: List<FaceExport>,
    val variants: List<VariantExport>
)

@Serializable
data class FaceExport(
    val name: String,
    val cost: Int,
    val power: Int? = null,
    val type: String
)

@Serializable
data class VariantExport(
    val variantName: String,
    val ownedCount: Int = 0,
    val imagePath: String? = null
)

@Serializable
data class DeckExport(
    val deckName: String,
    val createDate: String,
    val zoneType: String,
    val cards: List<DeckCardExport>
)

@Serializable
data class DeckCardExport(
    val cardName: String,
    val variantName: String,
    val count: Int,
    val zoneType: String
)

@Serializable
data class AppDataExport(
    val cards: List<CardExport>,
    val decks: List<DeckExport>
)
