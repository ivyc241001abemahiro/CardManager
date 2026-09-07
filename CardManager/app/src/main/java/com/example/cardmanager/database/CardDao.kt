package com.example.cardmanager.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCard(card: Card): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertVariant(variant: CardVariant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFace(face: CardFace)

    @Update
    abstract suspend fun updateCard(card: Card)

    @Delete
    abstract suspend fun deleteCard(card: Card)

    @Transaction
    @Query("SELECT * FROM cards")
    abstract fun getAllCardsWithVariants(): Flow<List<CardWithVariants>>

    @Transaction
    @Query("""
        SELECT DISTINCT cards.* FROM cards 
        LEFT JOIN card_faces ON cards.id = card_faces.cardId 
        WHERE card_faces.name LIKE '%' || :query || '%' OR cards.civilization LIKE '%' || :query || '%'
    """)
    abstract fun searchCards(query: String): Flow<List<CardWithVariants>>

    @Transaction
    @Query("SELECT * FROM cards WHERE id = :cardId")
    abstract suspend fun getCardWithVariantsById(cardId: Int): CardWithVariants?

    @Transaction
    @Query("SELECT * FROM cards")
    abstract suspend fun getAllCardsWithVariantsList(): List<CardWithVariants>

    @Transaction
    @Query("""
        SELECT DISTINCT cards.* FROM cards 
        LEFT JOIN card_faces ON cards.id = card_faces.cardId 
        WHERE card_faces.name = :name LIMIT 1
    """)
    abstract suspend fun getCardByName(name: String): Card?

    @Transaction
    @Query("SELECT * FROM card_variants WHERE cardId = :cardId AND variantName = :variantName LIMIT 1")
    abstract suspend fun getVariantByName(cardId: Int, variantName: String): CardVariant?

    @Query("DELETE FROM card_variants WHERE cardId = :cardId")
    abstract suspend fun deleteVariantsByCardId(cardId: Int)

    @Query("DELETE FROM card_faces WHERE cardId = :cardId")
    abstract suspend fun deleteFacesByCardId(cardId: Int)

    @Transaction
    open suspend fun insertCardWithData(card: Card, variants: List<CardVariant>, faces: List<CardFace>) {
        val generatedCardId = insertCard(card).toInt()
        variants.forEach { variant ->
            insertVariant(variant.copy(id = 0, cardId = generatedCardId))
        }
        faces.forEach { face ->
            insertFace(face.copy(id = 0, cardId = generatedCardId))
        }
    }

    @Transaction
    open suspend fun updateCardWithData(card: Card, variants: List<CardVariant>, faces: List<CardFace>) {
        updateCard(card)
        deleteVariantsByCardId(card.id)
        deleteFacesByCardId(card.id)
        variants.forEach { variant ->
            insertVariant(variant.copy(id = 0, cardId = card.id))
        }
        faces.forEach { face ->
            insertFace(face.copy(id = 0, cardId = card.id))
        }
    }
}

data class CardWithVariants(
    @Embedded val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val variants: List<CardVariant>,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val faces: List<CardFace>
)
