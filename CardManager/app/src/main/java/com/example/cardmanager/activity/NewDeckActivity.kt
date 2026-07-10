package com.example.cardmanager.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cardmanager.R
import com.example.cardmanager.database.AppDatabase
import com.example.cardmanager.database.Deck
import kotlinx.coroutines.launch

class NewDeckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_deck)

        val editDeckName = findViewById<EditText>(R.id.editDeckName)
        val btnCreate = findViewById<Button>(R.id.btnCreate)

        btnCreate.setOnClickListener {

            val db = AppDatabase.getDatabase(this)

            lifecycleScope.launch {
                db.deckDao().insert(
                    Deck(
                        name = editDeckName.text.toString()
                    )
                )
                finish()
            }
        }
    }
}