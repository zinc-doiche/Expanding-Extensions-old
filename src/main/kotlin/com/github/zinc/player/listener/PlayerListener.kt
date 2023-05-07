package com.github.zinc.player.listener

import com.github.zinc.player.domain.PlayerContainer
import com.github.zinc.player.manager.PlayerStatusManager
import com.github.zinc.quest.event.QuestClearEvent
import com.github.zinc.quest.manager.QuestManager
import com.github.zinc.util.extension.text
import com.github.zinc.util.sound.Sounds
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Enemy
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class PlayerListener: Listener {
    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val player: Player = when(e.damager) {
            is Player -> e.damager as Player
            is AbstractArrow -> {
                val abstractArrow = e.damager as AbstractArrow
                val shooter = abstractArrow.shooter ?: return

                shooter as Player
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