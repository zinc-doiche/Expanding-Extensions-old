package com.github.zinc.core.player.listener

import com.github.zinc.core.player.dao.PlayerDAO
import com.github.zinc.core.player.manager.PlayerStatusManager
import com.github.zinc.core.quest.manager.QuestManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerStatusListener: Listener {

    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent) {
        val manager = PlayerStatusManager(PlayerContainer[e.player.name]!!)
        manager.applyAll()
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        var isNewbie = false
        PlayerDAO().use { dao ->
            val playerDTO = dao.select(e.player)?.of(e.player) ?: run {
                dao.insert(e.player)
                isNewbie = true
                dao.select(e.player)?.of(e.player) ?: run {
                    e.player.kick()
                    return
                }
            }
            if(isNewbie) QuestManager.registerAllQuests(playerDTO)
            PlayerContainer.add(e.player.name, playerDTO)
            PlayerStatusManager(playerDTO).applyAll()
        }

        QuestManager.clearMap[e.player.name] = hashSetOf()
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val playerDTO = PlayerContainer.remove(e.player.name) ?: return
        PlayerDAO().use { it.update(playerDTO) }

        QuestManager.clearMap.remove(e.player.name)
    }
}