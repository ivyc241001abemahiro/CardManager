package com.example.cardmanager.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cardmanager.R
import com.example.cardmanager.adapter.CardAdapter
import com.example.cardmanager.database.AppDatabase
import kotlinx.coroutines.launch

class CardListActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)

        recycler = findViewById(R.id.recyclerCard)
        recycler.layoutManager = LinearLayoutManager(this)

        val btnAdd = findViewById<Button>(R.id.btnAddCard)

        btnAdd.setOnClickListener {
            startActivity(
                Intent(this, NewCardActivity::class.java)
            )
        }

        loadCards()
    }

    override fun onResume() {
        super.onResume()
        loadCards()
    }

    private fun loadCards() {

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {

            val list = db.cardFaceDao().getAll()

            adapter = CardAdapter(list)

            recycler.adapter = adapter
        }
    }
}