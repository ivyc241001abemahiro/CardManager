package com.example.cardmanager.model

data class FaceInput(

    var name: String = "",

    // 文明
    var civilizations: MutableList<String> = mutableListOf(),

    // コスト
    var cost: Int = 0,

    // カードタイプ
    var type: String = "",

    // 種族
    var race: String = "",

    // パワー
    var power: Int? = null,

    // 能力
    var ability: String = ""

)