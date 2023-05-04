package com.github.zinc.player.listener

import com.github.zinc.player.PlayerContainer
import com.github.zinc.player.event.PlayerGetExpEvent
import com.github.zinc.player.manager.PlayerStatusManager
import com.github.zinc.quest.QuestContainer
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
        if(e.damager !is Player || e.entity !is Enemy) return

        val enemy = e.entity as Enemy
        if(enemy.health > e.finalDamage) return

        val player = e.damager as Player
        val quest = QuestContainer[player] ?: run { QuestContainer.add(player, hashMapOf(enemy.name to 1)); player.sendMessage("${enemy.name}: (1/5)"); return }
        var number = quest[enemy.name] ?: run { quest[enemy.name] = 1; player.sendMessage("${enemy.name}: (1/5)"); return }
        if(number == -1) { player.sendMessage("§6이미 ${enemy.name} 퀘스트를 완료하였습니다. 퀘스트는 매일 오전 2시에 초기화됩니다."); return }
        player.sendMessage("${enemy.name}: (${++number}/5)")

        if(number >= 5) {
            quest[enemy.name] = -1
            PlayerGetExpEvent(player, 100 ?: run { player.sendMessage("이 메세지가 보이면 나한테 당장 말하셈"); return }).callEvent()
            player.sendMessage("\n${enemy.name} 퀘스트 완료! (+100 xp)\n")
            return
        }
        quest[enemy.name] = number
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