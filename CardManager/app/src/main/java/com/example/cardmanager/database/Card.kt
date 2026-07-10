package com.example.cardmanager.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // カード番号
    val cardNumber: String? = null,

    // イラスト
    val imagePath: String? = null,

    // レアリティ
    val rarity: String? = null,

    // パック
    val setName: String? = null,

    // 所持枚数
    val ownCount: Int = 0,

    // 使用可能枚数
    val usableCount: Int = 4
)