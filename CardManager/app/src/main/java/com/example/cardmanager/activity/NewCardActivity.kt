package com.example.cardmanager.activity

import android.R.attr.type
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cardmanager.R
import com.example.cardmanager.database.AppDatabase
import com.example.cardmanager.database.Card
import com.example.cardmanager.database.CardFace
import kotlinx.coroutines.launch

class NewCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_card)

        val editName = findViewById<EditText>(R.id.editCardName)
        val editCost = findViewById<EditText>(R.id.editCost)
        val editRace = findViewById<EditText>(R.id.editRace)
        val editPower = findViewById<EditText>(R.id.editPower)

        // Spinner
        val spinnerType = findViewById<Spinner>(R.id.spinnerType)

        val types = arrayOf(
            "クリーチャー",
            "呪文",
            "クロスギア",
            "城",
        )

        spinnerType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        // CheckBox
        val chkLight = findViewById<CheckBox>(R.id.chkLight)
        val chkWater = findViewById<CheckBox>(R.id.chkWater)
        val chkDark = findViewById<CheckBox>(R.id.chkDark)
        val chkFire = findViewById<CheckBox>(R.id.chkFire)
        val chkNature = findViewById<CheckBox>(R.id.chkNature)
        val chkZero = findViewById<CheckBox>(R.id.chkZero)

        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {

            // 文明を取得
            val civilizations = mutableListOf<String>()

            if (chkLight.isChecked) civilizations.add("光")
            if (chkWater.isChecked) civilizations.add("水")
            if (chkDark.isChecked) civilizations.add("闇")
            if (chkFire.isChecked) civilizations.add("火")
            if (chkNature.isChecked) civilizations.add("自然")
            if (chkZero.isChecked) civilizations.add("無色")

            val civilization = civilizations.joinToString(",")

            // カードタイプ
            val type = spinnerType.selectedItem.toString()

            lifecycleScope.launch {

                val db = AppDatabase.getDatabase(this@NewCardActivity)

                // Cardを保存
                val cardId = db.cardDao().insert(
                    Card(
                        ownCount = 0,
                        usableCount = 4
                    )
                ).toInt()

                // Face①を保存
                db.cardFaceDao().insert(

                    CardFace(

                        cardId = cardId,

                        faceNo = 1,

                        name = editName.text.toString(),

                        civilization = civilization,

                        cost = editCost.text.toString().toIntOrNull() ?: 0,

                        type = type,

                        race = editRace.text.toString(),

                        power = editPower.text.toString().toIntOrNull()

                    )

                )

                finish()

            }

        }
    }
}