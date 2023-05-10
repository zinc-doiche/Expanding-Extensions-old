package com.github.zinc.front.listener

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import com.github.zinc.container.PlayerContainer
import com.github.zinc.core.player.dao.PlayerDAO
import com.github.zinc.core.player.PlayerData
import com.github.zinc.core.player.manager.PlayerStatusManager
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
    }

    @EventHandler
    fun onRespawn(e: PlayerPostRespawnEvent) {
        val manager = PlayerStatusManager(PlayerContainer[e.player.name]!!)
        manager.applyAll()
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

        val playerDTO = PlayerContainer[player.name]!!
        val manager = PlayerStatusManager(playerDTO)
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

        QuestClearEvent(playerDTO, enemy).callEvent()
    }

}