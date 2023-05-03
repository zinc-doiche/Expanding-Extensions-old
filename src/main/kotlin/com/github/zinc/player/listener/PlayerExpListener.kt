package com.github.zinc.player.listener

import com.github.zinc.player.PlayerContainer
import com.github.zinc.player.event.PlayerGetExpEvent
import com.github.zinc.player.event.PlayerLevelUpEvent
import com.github.zinc.player.manager.PlayerStatusManager
import com.github.zinc.quest.QuestContainer
import com.github.zinc.warn
import org.bukkit.entity.Enemy
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

/**
 * TODO :
 *  [Replace]
 *  1) mob -> daily quest (15)
 *  2) exp bottle =
 *  3) furnace X
 *  4) ore & sculk O, X
 *  5) trade -> daily once?
 *
 * TODO :
 *  [Add]
 *  1) achievements
 *  2) loot(opening the chest)
 *  3) original exp -> exp
 */
class PlayerExpListener: Listener {
    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if(e.damager !is Player) return
        if(!e.entity.isDead) return
        if(e.entityType.entityClass !is Enemy) return
        e.damager.sendMessage(e.entity.name)

        QuestContainer[e.damager as Player]?.let { map ->
            map[e.entityType.entityClass as Enemy]?.let { number ->
                if(number + 1 >= 15) {
                    map[e.entityType.entityClass as Enemy] = 15
                    return
                }
                map[e.entityType.entityClass as Enemy] = number + 1
            } ?: run { map[e.entityType.entityClass as Enemy] = 1 }
        } ?: run { warn("Quest of ${e.damager.name} is not exist."); return }
    }

    @EventHandler
    fun onLevelUp(e: PlayerLevelUpEvent) {

    }

    @EventHandler
    fun onGetExperience(e: PlayerGetExpEvent) {
        val playerDTO = PlayerContainer[e.player.name] ?: return
        val manager = PlayerStatusManager(playerDTO)
        var maxExp = manager.getMaxExpForNextLevel()
        var currExp = e.amount + (playerDTO.playerExperience)

        if(maxExp > currExp) {
            manager.expUp(e.amount)
            return
        }

        while(maxExp <= currExp) {
            currExp -= maxExp
            maxExp = manager.getMaxExpForNextLevel()

            manager.levelUp()
        }
        manager.expUp(currExp)
    }
}