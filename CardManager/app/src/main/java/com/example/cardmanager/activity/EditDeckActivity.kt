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

class EditDeckActivity : AppCompatActivity() {

    private lateinit var deck: Deck

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_deck)

        val editName = findViewById<EditText>(R.id.editDeckName)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val db = AppDatabase.getDatabase(this)

        // DeckListActivityから渡されたIDを受け取る
        val deckId = intent.getIntExtra("deckId", -1)

        lifecycleScope.launch {

            // データベースからデッキを取得
            deck = db.deckDao().getDeck(deckId)

            // 現在の名前を表示
            editName.setText(deck.name)
        }

        // 保存ボタン
        btnSave.setOnClickListener {

            lifecycleScope.launch {

                db.deckDao().update(
                    deck.copy(
                        name = editName.text.toString()
                    )
                )

                finish()
            }
        }
    }
}