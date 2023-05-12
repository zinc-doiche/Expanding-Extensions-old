package com.github.zinc.front.listener

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import com.github.zinc.container.PlayerContainer
import com.github.zinc.core.player.dao.PlayerDAO
import com.github.zinc.core.player.PlayerData
import com.github.zinc.core.player.manager.PlayerStatusManager
import com.github.zinc.core.quest.dao.QuestDAO
import com.github.zinc.front.event.QuestClearEvent
import com.github.zinc.core.quest.manager.QuestManager
import com.github.zinc.util.extension.text
import com.github.zinc.util.Sounds
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Enemy
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.HashSet

class PlayerListener: Listener {
    @EventHandler
    fun onLogin(e: AsyncPlayerPreLoginEvent) {
        var isNewbie = false
        val playerName = e.playerProfile.name ?: return

        val playerVO = PlayerDAO().use { dao ->
             dao.select(playerName) ?: run {
                isNewbie = true
                dao.insert(playerName)
                dao.select(playerName) ?: run {
                    e.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        text("Login Cancelled, please retry login.")
                    )
                    return
                }
            }
        }

        if(isNewbie) QuestManager.registerAllQuests(playerVO.playerId)
        PlayerContainer.add(playerName, PlayerData(playerVO))

        QuestManager.clearMap[e.playerProfile.name!!] = QuestDAO().use { dao ->
            val questList = dao.selectList(playerVO.playerId) ?: return

            questList.filter { it.appendedQuestCleared }.map { it.appendedQuestName }.toSet()

        } as HashSet<String>
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val playerData = PlayerContainer[e.player.name]!!
        playerData.manager = PlayerStatusManager(playerData, e.player)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val playerData = PlayerContainer.remove(e.player.name) ?: return
        PlayerDAO().use { it.update(playerData.playerVO) }

        QuestManager.clearMap.remove(e.player.name)
    }

    @EventHandler
    fun onRespawn(e: PlayerPostRespawnEvent) {
        PlayerContainer[e.player.name]!!.manager?.applyAll() ?: return
    }

    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val player: Player = when(e.damager) {
            is Player -> e.damager as Player
            is AbstractArrow -> {
                val abstractArrow = e.damager as AbstractArrow
                val shooter = abstractArrow.shooter ?: return
                if(shooter !is Player) return
                shooter
            }
            else -> return
        }

        if(e.entity !is LivingEntity) return

        val playerData = PlayerContainer[player.name]!!
        val manager = playerData.manager ?: return
        e.damage = if(manager.rollCritical()) {
            player.playSound(Sounds.ironGolemDamaged)
            e.damage * 1.8
        } else e.damage

        player.sendMessage(text("${e.finalDamage}"))

        if(e.entity !is Enemy) return

        val enemy = e.entity as Enemy
        if(enemy.health > e.finalDamage) return

        if(QuestManager.clearMap[player.name]!!.contains(enemy.name)) {
            player.sendMessage("§6이미 ${enemy.name} 퀘스트를 완료하였습니다. 퀘스트는 매일 오전 2시에 초기화됩니다.")
            return
        }

        QuestClearEvent(playerData, enemy).callEvent()
    }

}