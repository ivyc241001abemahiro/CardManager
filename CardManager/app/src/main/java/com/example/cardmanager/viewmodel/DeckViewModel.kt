package com.example.cardmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cardmanager.database.Deck
import com.example.cardmanager.database.DeckCardCrossRef
import com.example.cardmanager.database.DeckCardInfo
import com.example.cardmanager.database.DeckWithCards
import com.example.cardmanager.repository.DeckRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeckViewModel(private val repository: DeckRepository) : ViewModel() {

    val deckList: StateFlow<List<Deck>> = repository.allDecks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards?> {
        return repository.getDeckWithCards(deckId)
    }

    fun getDeckCardsWithInfo(deckId: Int): Flow<List<DeckCardInfo>> {
        return repository.getDeckCardsWithInfo(deckId)
    }

    suspend fun saveDeck(deck: Deck) {
        if (deck.id == 0) {
            repository.insertDeck(deck)
        } else {
            repository.updateDeck(deck)
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
        }
    }

    fun addCardToDeck(deckId: Int, variantId: Int, count: Int, zoneType: String) {
        viewModelScope.launch {
            val crossRef = DeckCardCrossRef(deckId, variantId, count, zoneType)
            repository.addCardToDeck(crossRef)
        }
    }

    fun removeCardFromDeck(crossRef: DeckCardCrossRef) {
        viewModelScope.launch {
            repository.removeCardFromDeck(crossRef)
        }
    }

    fun validateDeck(cardInfoList: List<DeckCardInfo>): String? {
        // 1. メインデッキ枚数チェック (40枚固定)
        val mainDeckCount = cardInfoList.filter { it.zoneType == "Main" }.sumOf { it.count }
        if (mainDeckCount != 40) {
            return "メインデッキは40枚である必要があります (現在: ${mainDeckCount}枚)"
        }

        // 2. GRデッキ枚数チェック (0枚または12枚)
        val grCount = cardInfoList.filter { it.zoneType == "GR" }.sumOf { it.count }
        if (grCount != 0 && grCount != 12) {
            return "GRデッキは0枚または12枚である必要があります (現在: ${grCount}枚)"
        }
        
        // 3. 超次元デッキ枚数チェック (最大8枚)
        val psychicCount = cardInfoList.filter { it.zoneType == "Psychic" }.sumOf { it.count }
        if (psychicCount > 8) {
            return "超次元デッキは最大8枚までです (現在: ${psychicCount}枚)"
        }

        // 4. 同名カード枚数制限チェック
        val cardCounts = cardInfoList.groupBy { it.cardId }
        for ((_, infos) in cardCounts) {
            val totalForCard = infos.sumOf { it.count }
            val maxAllowed = infos.first().maxCount
            if (totalForCard > maxAllowed) {
                return "${infos.first().cardName} は最大 ${maxAllowed} 枚までです (現在: ${totalForCard}枚)"
            }
        }

        return null
    }

    class Factory(private val repository: DeckRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DeckViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DeckViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
