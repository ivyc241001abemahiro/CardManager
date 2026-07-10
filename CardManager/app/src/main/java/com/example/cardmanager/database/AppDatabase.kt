package com.example.cardmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Deck::class,
        Card::class,
        CardFace::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun cardFaceDao(): CardFaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "card_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}