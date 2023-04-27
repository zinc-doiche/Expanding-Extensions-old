package com.github.zinc.player.domain

data class PlayerVO(
    var playerId: Long? = null,
    var playerName: String? = null,
    var playerLevel: Int? = null, //addit stat = level * 3 | max 100lv = 303 stats.
    var playerStatusRemain: Int? = null, //3 default. | max total stats = 600.
    // stat's branch : 150 , 250 , 400 , 550
    // 실질스탯 효과는 250에서 거의 끝난다고 보면 된다. 이후는 제한스탯을 뚫는 용도.
    var playerStrength: Int? = null,
    var playerSwiftness: Int? = null,
    var playerConcentration: Int? = null,
    var playerBalance: Int? = null
)
