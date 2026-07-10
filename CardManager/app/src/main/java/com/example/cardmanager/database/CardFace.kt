package com.example.cardmanager.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "card_faces",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CardFace(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Card.id
    val cardId: Int,

    // 1面目・2面目・3面目
    val faceNo: Int,

    // 名前
    val name: String,

    // 文明
    val civilization: String,

    // コスト
    val cost: Int,

    // タイプ
    val type: String,

    // 種族
    val race: String,

    // パワー
    val power: Int? = null
)