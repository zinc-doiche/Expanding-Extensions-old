package com.github.zinc.core.quest.listener

import com.github.zinc.info
import com.github.zinc.core.player.domain.PlayerContainer
import com.github.zinc.core.player.event.PlayerGetExpEvent
import com.github.zinc.core.quest.dao.QuestDAO
import com.github.zinc.core.quest.event.QuestClearEvent
import com.github.zinc.core.quest.manager.QuestManager
import com.github.zinc.util.sound.Sounds
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

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
    fun onQuestClear(e: QuestClearEvent) {
        QuestDAO().use { dao ->
            info("qc, ${PlayerContainer[e.player.name]!!.playerId}, ${e.enemy.name}")
            val questDTO = dao.select(PlayerContainer[e.player.name]!!.playerId, e.enemy) ?: return@use
            if(questDTO.appendedQuestCleared) {
                e.player.sendMessage("§6이미 ${e.enemy.name} 퀘스트를 완료하였습니다. 퀘스트는 매일 오전 2시에 초기화됩니다.")
                QuestManager.clearMap[e.player.name]?.add(e.enemy.name)
                return@use
            }
            e.player.sendMessage("§4${e.enemy.name}: (${++questDTO.appendedQuestProgress}/${questDTO.questRequire})")

            if(questDTO.appendedQuestProgress >= questDTO.questRequire) {
                questDTO.appendedQuestCleared = true
                e.player.playSound(Sounds.questClear)
                PlayerGetExpEvent(e.player, questDTO.questReward).callEvent()
                e.player.sendMessage("\n§6${e.enemy.name} 퀘스트 완료! (+${questDTO.questReward} xp)\n ")
            }

            dao.update(questDTO)
        }
    }
}