package com.example.cardmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cardmanager.database.Card
import com.example.cardmanager.database.CardFace
import com.example.cardmanager.database.CardVariant
import com.example.cardmanager.database.CardWithVariants
import com.example.cardmanager.repository.CardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CardViewModel(private val repository: CardRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val cardList: StateFlow<List<CardWithVariants>> = _searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allCards
            } else {
                repository.searchCards(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    suspend fun getCardById(id: Int): CardWithVariants? {
        return repository.getCardById(id)
    }

    suspend fun saveCard(card: Card, variants: List<CardVariant>, faces: List<CardFace>) {
        if (card.id == 0) {
            repository.insertCard(card, variants, faces)
        } else {
            repository.updateCard(card, variants, faces)
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    class Factory(private val repository: CardRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CardViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
