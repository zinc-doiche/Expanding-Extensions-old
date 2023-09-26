package com.github.zinc.front.listener

import com.github.zinc.lib.event.PlayerGetExpEvent
import com.github.zinc.lib.event.QuestClearEvent
import com.github.zinc.util.PassedBy
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
class PlayerExpListener: Listener {

    @EventHandler
    @PassedBy(QuestListener::class, QuestClearEvent::class)
    fun onGetExperience(e: PlayerGetExpEvent) {
//        val playerData = PlayerContainer[e.player.name] ?: return
//        e.player.location
//        val manager = playerData.manager ?: return
//        var maxExp = manager.getMaxExpForNextLevel()
//        var currExp = e.amount + playerData.playerVO.playerExperience
//
//        if(maxExp > currExp) {
//            manager.expUp(e.amount)
//            return
//        }
//        while(maxExp <= currExp) {
//            currExp -= maxExp
//            maxExp = manager.getMaxExpForNextLevel()
//
//            manager.levelUp()
//        }
//        manager.playerEntity.playSound(Sounds.levelUp)
//        manager.setExp(0)
//        manager.expUp(currExp)
    }
}