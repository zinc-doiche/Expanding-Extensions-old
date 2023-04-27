package com.github.zinc.player.dao

import com.github.zinc.player.domain.PlayerDTO
import org.bukkit.entity.Player

object PlayerDAO {
    fun selectOne(player: Player): PlayerDTO? {
        val playerName = player.name
        //TODO select in DB
        return PlayerDTO()
    }

    fun insert(player: Player) {

    }

    fun update(playerDTO: PlayerDTO) {

    }
}