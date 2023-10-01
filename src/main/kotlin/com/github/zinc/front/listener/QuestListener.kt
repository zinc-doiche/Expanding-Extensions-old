package com.github.zinc.front.listener

import com.github.zinc.lib.event.PlayerGetExpEvent
import com.github.zinc.lib.event.QuestClearEvent
import com.github.zinc.util.ChainEventCall
import com.github.zinc.util.PassedBy
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
//    @PassedBy(PlayerListener::class, EntityDamageByEntityEvent::class)
    @ChainEventCall(PlayerGetExpEvent::class)
    fun onQuestClear(e: QuestClearEvent) {
//        val player = e.playerData.manager?.playerEntity ?: return
//        QuestDAO().use { dao ->
//            //info("qc, ${e.playerData.playerVO.playerId}, ${e.enemy.name}")
//            val questDTO = dao.select(e.playerData.playerVO.playerId, e.enemy) ?: return@use
//            if(questDTO.appendedQuestCleared) {
//                player.sendMessage("§6이미 ${e.enemy.name} 퀘스트를 완료하였습니다. 퀘스트는 매일 오전 2시에 초기화됩니다.")
//                QuestManager.clearMap[player.name]?.add(e.enemy.name)
//                return@use
//            }
//            player.sendMessage("§4${e.enemy.name}: (${++questDTO.appendedQuestProgress}/${questDTO.questRequire})")
//
//            if(questDTO.appendedQuestProgress >= questDTO.questRequire) {
//                questDTO.appendedQuestCleared = true
//                player.playSound(Sounds.questClear)
//                PlayerGetExpEvent(player, questDTO.questReward).callEvent()
//                player.sendMessage("\n§6${e.enemy.name} 퀘스트 완료! (+${questDTO.questReward} xp)\n ")
//            }
//
//            dao.update(questDTO)
//        }
    }
}