package com.example.cardmanager.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cardmanager.R
import com.example.cardmanager.adapter.FaceInputAdapter
import com.example.cardmanager.database.AppDatabase
import com.example.cardmanager.database.Deck
import com.example.cardmanager.model.FaceInput
import kotlinx.coroutines.launch

class EditDeckActivity : AppCompatActivity() {

    private lateinit var deck: Deck

    private lateinit var faceAdapter: FaceInputAdapter

    private val faceList = mutableListOf<FaceInput>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_deck)

        val editName = findViewById<EditText>(R.id.editDeckName)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnAddFace = findViewById<Button>(R.id.btnAddFace)
        val recyclerFaces = findViewById<RecyclerView>(R.id.recyclerFaces)

        val db = AppDatabase.getDatabase(this)

        // DeckListActivityから渡されたIDを受け取る
        val deckId = intent.getIntExtra("deckId", -1)

        // RecyclerView設定
        faceAdapter = FaceInputAdapter(faceList)

        recyclerFaces.layoutManager =
            LinearLayoutManager(this)

        recyclerFaces.adapter =
            faceAdapter

        lifecycleScope.launch {

            // データベースからデッキを取得
            deck = db.deckDao().getDeck(deckId)

            // 現在の名前を表示
            editName.setText(deck.name)

            // 最初は1面追加
            faceList.add(FaceInput())

            faceAdapter.notifyItemInserted(0)
        }

        // 面を追加
        btnAddFace.setOnClickListener {

            faceList.add(FaceInput())

            faceAdapter.notifyItemInserted(
                faceList.size - 1
            )

            // 追加した面までスクロール
            recyclerFaces.smoothScrollToPosition(
                faceList.size - 1
            )
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