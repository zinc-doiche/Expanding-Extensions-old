package com.github.zinc.player

import com.github.zinc.player.dao.PlayerDAO
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListener: Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val playerDTO = PlayerDAO.selectOne(e.player)

    }
}