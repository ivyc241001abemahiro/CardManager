package com.example.cardmanager.repository

import com.example.cardmanager.database.Card
import com.example.cardmanager.database.CardDao
import com.example.cardmanager.database.CardFace
import com.example.cardmanager.database.CardVariant
import com.example.cardmanager.database.CardWithVariants
import kotlinx.coroutines.flow.Flow

class CardRepository(private val cardDao: CardDao) {
    val allCards: Flow<List<CardWithVariants>> = cardDao.getAllCardsWithVariants()

    fun searchCards(query: String): Flow<List<CardWithVariants>> {
        return cardDao.searchCards(query)
    }

    suspend fun getCardById(cardId: Int): CardWithVariants? {
        return cardDao.getCardWithVariantsById(cardId)
    }

    suspend fun insertCard(card: Card, variants: List<CardVariant>, faces: List<CardFace>) {
        cardDao.insertCardWithData(card, variants, faces)
    }

    suspend fun insertCardDirect(card: Card): Long {
        return cardDao.insertCard(card)
    }

    suspend fun updateCard(card: Card, variants: List<CardVariant>, faces: List<CardFace>) {
        cardDao.updateCardWithData(card, variants, faces)
    }

    suspend fun deleteCard(card: Card) {
        cardDao.deleteCard(card)
    }

    suspend fun getAllCardsForExport(): List<CardWithVariants> {
        return cardDao.getAllCardsWithVariantsList()
    }

    suspend fun getCardByName(name: String): Card? = cardDao.getCardByName(name)

    suspend fun getVariantByName(cardId: Int, variantName: String): CardVariant? =
        cardDao.getVariantByName(cardId, variantName)

    suspend fun insertVariant(variant: CardVariant) = cardDao.insertVariant(variant)

    suspend fun insertFace(face: CardFace) = cardDao.insertFace(face)
}
