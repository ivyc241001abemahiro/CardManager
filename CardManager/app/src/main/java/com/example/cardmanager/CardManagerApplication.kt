package com.example.cardmanager

import android.app.Application
import com.example.cardmanager.database.AppDatabase
import com.example.cardmanager.repository.CardRepository
import com.example.cardmanager.repository.DeckRepository

class CardManagerApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val cardRepository: CardRepository by lazy { CardRepository(database.cardDao()) }
    val deckRepository: DeckRepository by lazy { DeckRepository(database.deckDao()) }
}
