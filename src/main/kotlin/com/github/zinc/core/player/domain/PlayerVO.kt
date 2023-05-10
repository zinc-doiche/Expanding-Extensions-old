package com.github.zinc.core.player.domain

import org.bukkit.entity.Player

data class PlayerVO(
    var playerId: Long,
    var playerName: String,
    var playerLevel: Int, //addit stat = level * 3 | max 100lv = 303 stats.
    var playerExperience: Int,
    var playerStatusRemain: Int, //3 default. | max total stats = 600.
    // stat's branch : 150 , 250 , 400 , 550
    // 실질스탯 효과는 250에서 거의 끝난다고 보면 된다. 이후는 제한스탯을 뚫는 용도.
    var playerStrength: Int,
    var playerSwiftness: Int,
    var playerConcentration: Int,
    var playerBalance: Int
) {
    fun of(player: Player) = PlayerDTO(
        playerId, playerName, playerLevel, playerExperience, playerStatusRemain,
        playerStrength, playerSwiftness, playerConcentration, playerBalance, player
    )

    override fun toString(): String {
        return "PlayerVO(playerId=$playerId, playerName='$playerName', playerLevel=$playerLevel, playerExperience=$playerExperience, playerStatusRemain=$playerStatusRemain, playerStrength=$playerStrength, playerSwiftness=$playerSwiftness, playerConcentration=$playerConcentration, playerBalance=$playerBalance)"
    }
}
