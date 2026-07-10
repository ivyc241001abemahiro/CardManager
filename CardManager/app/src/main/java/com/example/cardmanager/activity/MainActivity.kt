package com.example.cardmanager.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cardmanager.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnDeck = findViewById<Button>(R.id.btnDeck)
        val btnCard = findViewById<Button>(R.id.btnCard)

        btnDeck.setOnClickListener {
            startActivity(Intent(this, DeckListActivity::class.java))
        }

        btnCard.setOnClickListener {
            startActivity(Intent(this, CardListActivity::class.java))
        }
    }
}