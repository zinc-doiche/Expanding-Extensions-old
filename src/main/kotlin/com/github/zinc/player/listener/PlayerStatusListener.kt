package com.github.zinc.player.listener

import com.github.zinc.info
import com.github.zinc.player.PlayerContainer
import com.github.zinc.player.dao.PlayerDAO
import com.github.zinc.player.event.PlayerLevelUpEvent
import com.github.zinc.player.event.PlayerStatusChangeEvent
import com.github.zinc.player.manager.PlayerStatusManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerStatusListener: Listener {

    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent) {
        val manager = PlayerStatusManager(PlayerDAO().select(e.player)?.of(e.player) ?: return)
        manager.applyAll()
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        PlayerDAO().use { dao ->
            val playerDTO = dao.select(e.player)?.of(e.player) ?: run {
                dao.insert(e.player)
                dao.select(e.player)?.of(e.player) ?: run {
                    e.player.kick()
                    return
                }
            }
            PlayerContainer.add(e.player.name, playerDTO)
            PlayerStatusManager(playerDTO).applyAll()
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val playerDTO = PlayerContainer.remove(e.player.name) ?: return
        PlayerDAO().update(playerDTO)
    }

    @EventHandler
    fun onLevelUp(e: PlayerLevelUpEvent) {
        PlayerContainer[e.player.name]!!.playerStatusRemain++
    }

    @EventHandler
    fun onGetExperience(e: PlayerExpChangeEvent) {
        e.player.sendMessage(e.amount.toString())
        val playerDTO = PlayerContainer[e.player.name] ?: return
        val manager = PlayerStatusManager(playerDTO)
        var maxExp = PlayerStatusManager.getMaxExpForNextLevel(playerDTO.playerLevel)
        val currExp = e.amount + (playerDTO.playerExperience)

        if(maxExp <= currExp) {
            var excess = currExp - maxExp
            maxExp = PlayerStatusManager.getMaxExpForNextLevel(playerDTO.playerLevel)
            manager.levelUp()
            if(maxExp > excess) {
                playerDTO.playerExperience = excess
                return
            }
            while (maxExp <= excess) {
                maxExp = PlayerStatusManager.getMaxExpForNextLevel(playerDTO.playerLevel)
                excess -= maxExp
                manager.levelUp()
            }
            playerDTO.playerExperience = excess
        } else manager.expUp(e.amount)
    }

    @EventHandler
    fun onStatusChange(e: PlayerStatusChangeEvent) {
        val playerDTO = PlayerContainer[e.player.name] ?: return
        val manager = PlayerStatusManager(playerDTO)
        e.changes.forEach {
            manager.updateStatus(it.toPair())
            manager.applyStatus(it.key)
        }
    }
}