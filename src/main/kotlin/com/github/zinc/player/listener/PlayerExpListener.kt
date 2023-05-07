package com.github.zinc.player.listener

import com.github.zinc.player.domain.PlayerContainer
import com.github.zinc.player.event.PlayerGetExpEvent
import com.github.zinc.player.manager.PlayerStatusManager
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
class PlayerExpListener: Listener {
    @EventHandler
    fun onGetExperience(e: PlayerGetExpEvent) {
        val playerDTO = PlayerContainer[e.player.name] ?: return
        val manager = PlayerStatusManager(playerDTO)
        var maxExp = manager.getMaxExpForNextLevel()
        var currExp = e.amount + playerDTO.playerExperience

        if(maxExp > currExp) {
            manager.expUp(e.amount)
            return
        }

        while(maxExp <= currExp) {
            currExp -= maxExp
            maxExp = manager.getMaxExpForNextLevel()

            manager.levelUp()
        }
        playerDTO.playerEntity.playSound(Sounds.levelUp)
        manager.setExp(0)
        manager.expUp(currExp)
    }
}