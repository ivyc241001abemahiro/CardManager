package com.example.cardmanager.repository

import com.example.cardmanager.database.Deck
import com.example.cardmanager.database.DeckCardCrossRef
import com.example.cardmanager.database.DeckCardInfo
import com.example.cardmanager.database.DeckDao
import com.example.cardmanager.database.DeckWithCards
import kotlinx.coroutines.flow.Flow

class DeckRepository(private val deckDao: DeckDao) {
    val allDecks: Flow<List<Deck>> = deckDao.getAllDecks()

    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards> {
        return deckDao.getDeckWithCards(deckId)
    }

    fun getDeckCardsWithInfo(deckId: Int): Flow<List<DeckCardInfo>> {
        return deckDao.getDeckCardsWithInfo(deckId)
    }

    suspend fun insertDeck(deck: Deck): Long {
        return deckDao.insertDeck(deck)
    }

    suspend fun updateDeck(deck: Deck) {
        deckDao.updateDeck(deck)
    }

    suspend fun deleteDeck(deck: Deck) {
        deckDao.deleteDeck(deck)
    }

    suspend fun addCardToDeck(crossRef: DeckCardCrossRef) {
        deckDao.insertDeckCardCrossRef(crossRef)
    }

    suspend fun removeCardFromDeck(crossRef: DeckCardCrossRef) {
        deckDao.deleteDeckCardCrossRef(crossRef)
    }

    suspend fun getAllDecksForExport(): List<Deck> = deckDao.getAllDecksList()

    suspend fun getDeckCardsForExport(deckId: Int): List<DeckCardInfo> =
        deckDao.getDeckCardsWithInfoList(deckId)
}
