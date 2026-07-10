package com.example.cardmanager.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cardmanager.R
import com.example.cardmanager.adapter.DeckAdapter
import com.example.cardmanager.database.AppDatabase
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import com.example.cardmanager.database.Deck
import kotlin.jvm.java
import com.example.cardmanager.activity.EditDeckActivity

class DeckListActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: DeckAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)

        recycler = findViewById(R.id.recyclerDeck)
        recycler.layoutManager = LinearLayoutManager(this)

        val btnAdd = findViewById<Button>(R.id.btnAddDeck)

        btnAdd.setOnClickListener {
            startActivity(
                Intent(this, NewDeckActivity::class.java)
            )
        }

        loadDecks()
    }

    override fun onResume() {
        super.onResume()

        // 新規デッキ画面から戻ってきたときに一覧を更新
        loadDecks()
    }

    private fun loadDecks() {

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {

            val list = db.deckDao().getAll()

            adapter = DeckAdapter(
                list,

                { deck ->
                    val intent = Intent(
                        this@DeckListActivity,
                        EditDeckActivity::class.java
                    )

                    intent.putExtra("deckId", deck.id)

                    startActivity(intent)
                },

                { deck ->
                    showDeleteDialog(deck)
                }
            )

            recycler.adapter = adapter
        }
    }

    private fun showDeleteDialog(deck: Deck) {

        AlertDialog.Builder(this)
            .setTitle("削除")
            .setMessage("${deck.name} を削除しますか？")

            .setPositiveButton("はい") { _, _ ->

                lifecycleScope.launch {

                    AppDatabase
                        .getDatabase(this@DeckListActivity)
                        .deckDao()
                        .delete(deck)

                    loadDecks()
                }
            }

            .setNegativeButton("いいえ", null)

            .show()
    }
}