package com.github.zinc.player.listener

import com.github.zinc.player.PlayerContainer
import com.github.zinc.player.dao.PlayerDAO
import com.github.zinc.player.event.PlayerStatusChangeEvent
import com.github.zinc.player.manager.PlayerStatusManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerStatusListener: Listener {

    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent) {
        val manager = PlayerStatusManager(PlayerDAO.selectOne(e.player) ?: return)
        manager.applyAll()
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val playerDTO = PlayerDAO.selectOne(e.player) ?: run {
            PlayerDAO.insert(e.player)
            PlayerDAO.selectOne(e.player) ?: run {
                e.player.kick()
                return
            }
        }
        PlayerContainer.add(e.player.name, playerDTO)

    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        PlayerContainer.remove(e.player.name)
    }

    @EventHandler
    fun onStatusChange(e: PlayerStatusChangeEvent) {
        val playerDTO = PlayerContainer[e.player.name] ?: return
        val manager = PlayerStatusManager(playerDTO)
        PlayerDAO.update(playerDTO)
        e.changes.forEach {
            manager.updateStatus(it.toPair())
            manager.applyStatus(it.key)
        }
    }
}