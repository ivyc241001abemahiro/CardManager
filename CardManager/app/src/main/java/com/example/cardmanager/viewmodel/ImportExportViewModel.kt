package com.example.cardmanager.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cardmanager.database.*
import com.example.cardmanager.repository.CardRepository
import com.example.cardmanager.repository.DeckRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

class ImportExportViewModel(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _status = MutableStateFlow<String?>(null)
    val status = _status.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun exportData(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cards = cardRepository.getAllCardsForExport().map { cardWithVariants ->
                    CardExport(
                        civilization = cardWithVariants.card.civilization,
                        maxCount = cardWithVariants.card.maxCount,
                        requiredCount = cardWithVariants.card.requiredCount,
                        faces = cardWithVariants.faces.map {
                            FaceExport(
                                name = it.name,
                                cost = it.cost,
                                power = it.power,
                                type = it.type
                            )
                        },
                        variants = cardWithVariants.variants.map {
                            VariantExport(
                                variantName = it.variantName,
                                ownedCount = it.ownedCount,
                                imagePath = it.imagePath
                            )
                        }
                    )
                }

                val decks = deckRepository.getAllDecksForExport().map { deck ->
                    val deckCards = deckRepository.getDeckCardsForExport(deck.id).map {
                        DeckCardExport(
                            cardName = it.cardName,
                            variantName = it.variantName,
                            count = it.count,
                            zoneType = it.zoneType
                        )
                    }
                    DeckExport(
                        deckName = deck.deckName,
                        createDate = deck.createDate,
                        zoneType = deck.zoneType,
                        cards = deckCards
                    )
                }

                val appData = AppDataExport(cards, decks)
                val jsonString = json.encodeToString(appData)

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
                _status.value = "エクスポート完了"
            } catch (e: Exception) {
                _status.value = "エクスポート失敗: ${e.message}"
            }
        }
    }

    fun importData(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).readText()
                } ?: throw Exception("ファイルが読み込めませんでした")

                val appData = json.decodeFromString<AppDataExport>(jsonString)

                _status.value = "インポート開始..."
                
                processImport(appData)

                _status.value = "インポート完了"
            } catch (e: Exception) {
                _status.value = "インポート失敗: ${e.message}"
            }
        }
    }

    private suspend fun processImport(appData: AppDataExport) {
        appData.cards.forEach { cardExport ->
            // 最初の面を代表名として検索
            val mainName = cardExport.faces.firstOrNull()?.name ?: ""
            val existingCard = cardRepository.getCardByName(mainName)
            val cardId = if (existingCard == null) {
                val newCard = Card(
                    civilization = cardExport.civilization,
                    maxCount = cardExport.maxCount,
                    requiredCount = cardExport.requiredCount
                )
                val newCardId = cardRepository.insertCardDirect(newCard).toInt()
                
                // フェイスの登録
                cardExport.faces.forEach { faceExport ->
                    cardRepository.insertFace(
                        CardFace(
                            cardId = newCardId,
                            name = faceExport.name,
                            cost = faceExport.cost,
                            power = faceExport.power,
                            type = faceExport.type
                        )
                    )
                }
                newCardId
            } else {
                existingCard.id
            }

            cardExport.variants.forEach { variantExport ->
                val existingVariant = cardRepository.getVariantByName(cardId, variantExport.variantName)
                if (existingVariant == null) {
                    cardRepository.insertVariant(
                        CardVariant(
                            cardId = cardId,
                            variantName = variantExport.variantName,
                            ownedCount = variantExport.ownedCount,
                            imagePath = variantExport.imagePath
                        )
                    )
                }
            }
        }

        appData.decks.forEach { deckExport ->
            val deckId = deckRepository.insertDeck(
                Deck(
                    deckName = deckExport.deckName,
                    createDate = deckExport.createDate,
                    zoneType = deckExport.zoneType
                )
            ).toInt()

            deckExport.cards.forEach { cardExport ->
                val card = cardRepository.getCardByName(cardExport.cardName)
                if (card != null) {
                    val variant = cardRepository.getVariantByName(card.id, cardExport.variantName)
                    if (variant != null) {
                        deckRepository.addCardToDeck(
                            DeckCardCrossRef(
                                deckId = deckId,
                                variantId = variant.id,
                                count = cardExport.count,
                                zoneType = cardExport.zoneType
                            )
                        )
                    }
                }
            }
        }
    }

    fun clearStatus() {
        _status.value = null
    }

    class Factory(
        private val cardRepository: CardRepository,
        private val deckRepository: DeckRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ImportExportViewModel(cardRepository, deckRepository) as T
        }
    }
}
