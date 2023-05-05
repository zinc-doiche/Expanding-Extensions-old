package com.github.zinc.quest.listener

import com.github.zinc.player.domain.PlayerContainer
import com.github.zinc.player.event.PlayerGetExpEvent
import com.github.zinc.quest.dao.QuestDAO
import com.github.zinc.quest.manager.QuestManager
import net.kyori.adventure.text.format.TextColor
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
class QuestListener: Listener {
    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if(e.damager !is Player || e.entity !is Enemy) return
        val enemy = e.entity as Enemy
        if(enemy.health >= e.finalDamage) return

        val player = e.damager as Player

        if(QuestManager.clearMap[player.name]!!.contains(enemy.name)) {
            player.sendMessage("§6이미 ${enemy.name} 퀘스트를 완료하였습니다. 퀘스트는 매일 오전 2시에 초기화됩니다.")
            return
        }

        QuestDAO().use { dao ->
            val questDTO = dao.select(PlayerContainer[player.name]!!.playerId, enemy) ?: return@use

            if(questDTO.appendedQuestCleared) {
                player.sendMessage("§6이미 ${enemy.name} 퀘스트를 완료하였습니다. 퀘스트는 매일 오전 2시에 초기화됩니다.")
                QuestManager.clearMap[player.name]?.add(enemy.name)
                return@use
            }
            player.sendMessage("§4${enemy.name}: (${++questDTO.appendedQuestProgress}/${questDTO.questRequire})")

            if(questDTO.appendedQuestProgress >= questDTO.questRequire) {
                questDTO.appendedQuestCleared = true
                PlayerGetExpEvent(player, questDTO.questReward).callEvent()
                player.sendMessage("\n§6${enemy.name} 퀘스트 완료! (+${questDTO.questReward} xp)\n ")
            }

            dao.update(questDTO)
        }
    }
}