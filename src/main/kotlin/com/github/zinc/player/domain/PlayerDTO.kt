package com.github.zinc.player.domain

import org.bukkit.entity.Player

data class PlayerDTO(
    var playerId: Long? = null,
    var playerName: String? = null,
    var playerLevel: Int? = null,
    var playerStatusRemain: Int? = null,
    var playerStrength: Int? = null,
    var playerSwiftness: Int? = null,
    var playerConcentration: Int? = null,
    var playerBalance: Int? = null,
    var playerEntity: Player? = null
)
