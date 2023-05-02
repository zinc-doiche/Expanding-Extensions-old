package com.github.zinc.player.domain

import org.bukkit.entity.Player

data class PlayerDTO(
    var playerId: Long,
    var playerName: String,
    var playerLevel: Int,
    var playerExperience: Int,
    var playerStatusRemain: Int,
    var playerStrength: Int,
    var playerSwiftness: Int,
    var playerConcentration: Int,
    var playerBalance: Int,
    var playerEntity: Player
) {
    override fun toString(): String {
        return "PlayerDTO(playerId=$playerId, playerName='$playerName', playerLevel=$playerLevel, playerExperience=$playerExperience, playerStatusRemain=$playerStatusRemain, playerStrength=$playerStrength, playerSwiftness=$playerSwiftness, playerConcentration=$playerConcentration, playerBalance=$playerBalance, playerEntity=$playerEntity)"
    }

    fun hasRemain() = playerStatusRemain > 0;
}
