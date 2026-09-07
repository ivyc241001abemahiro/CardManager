package com.example.cardmanager.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck): Long

    @Update
    suspend fun updateDeck(deck: Deck)

    @Delete
    suspend fun deleteDeck(deck: Deck)

    @Query("SELECT * FROM decks ORDER BY createDate DESC")
    fun getAllDecks(): Flow<List<Deck>>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getDeckById(id: Int): Deck?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeckCardCrossRef(crossRef: DeckCardCrossRef)

    @Delete
    suspend fun deleteDeckCardCrossRef(crossRef: DeckCardCrossRef)

    @Query("SELECT * FROM deck_card_cross_ref WHERE deckId = :deckId")
    fun getCardsForDeck(deckId: Int): Flow<List<DeckCardCrossRef>>

    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards>

    @Query("""
        SELECT 
            dc.deckId, 
            dc.variantId, 
            dc.count, 
            dc.zoneType,
            cv.variantName,
            cv.imagePath,
            cv.cardId,
            (SELECT group_concat(name, '/') FROM card_faces WHERE cardId = c.id) AS cardName,
            c.maxCount
        FROM deck_card_cross_ref dc
        JOIN card_variants cv ON dc.variantId = cv.id
        JOIN cards c ON cv.cardId = c.id
        WHERE dc.deckId = :deckId
    """)
    fun getDeckCardsWithInfo(deckId: Int): Flow<List<DeckCardInfo>>

    @Query("SELECT * FROM decks")
    suspend fun getAllDecksList(): List<Deck>

    @Query("""
        SELECT 
            dc.deckId, 
            dc.variantId, 
            dc.count, 
            dc.zoneType,
            cv.variantName,
            cv.imagePath,
            cv.cardId,
            (SELECT group_concat(name, '/') FROM card_faces WHERE cardId = c.id) AS cardName,
            c.maxCount
        FROM deck_card_cross_ref dc
        JOIN card_variants cv ON dc.variantId = cv.id
        JOIN cards c ON cv.cardId = c.id
        WHERE dc.deckId = :deckId
    """)
    suspend fun getDeckCardsWithInfoList(deckId: Int): List<DeckCardInfo>
}

data class DeckCardInfo(
    val deckId: Int,
    val variantId: Int,
    val count: Int,
    val zoneType: String,
    val variantName: String,
    val imagePath: String?,
    val cardId: Int,
    val cardName: String,
    val maxCount: Int
)

data class DeckWithCards(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<DeckCardCrossRef>
)
