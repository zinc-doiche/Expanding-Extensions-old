package com.github.zinc.quest.fx

import com.github.zinc.player.domain.PlayerDTO
import com.github.zinc.util.extension.item
import com.github.zinc.util.extension.text
import com.github.zinc.util.extension.texts
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.frame.InvList
import io.github.monun.invfx.openFrame
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object QuestFx {
    private val creeperIcon = ItemStack(Material.CREEPER_HEAD)
    private val returnIcon = item(Material.PAPER) {}

    private fun getHeadIcon(player: Player) = item(Material.PLAYER_HEAD) { meta ->
        (meta as SkullMeta).owningPlayer = player
        meta.displayName(text("일일퀘스트 목록 바로가기"))
    }

    fun getQuestMainFx(playerDTO: PlayerDTO): InvFrame {
        return InvFX.frame(1, text("${playerDTO.playerEntity.name}의 퀘스트 목록")) {
            slot(0, 0) {
                item = getHeadIcon(playerDTO.playerEntity)
                onClick { playerDTO.playerEntity.openFrame(getDailyQuestListFx(playerDTO)) }
            }
            slot(1, 0) {
                item = creeperIcon
                onClick { playerDTO.playerEntity.openFrame(getLimitedQuestListFx(playerDTO)) }
            }
        }
    }

    private fun getDailyQuestListFx(playerDTO: PlayerDTO): InvFrame {
        return InvFX.frame(6, text("${playerDTO.playerEntity.name}의 일일퀘스트 목록")) {
            slot(8, 5) {
                item = returnIcon
                onClick { playerDTO.playerEntity.openFrame(getQuestMainFx(playerDTO)) }
            }
        }
    }

    private fun getLimitedQuestListFx(playerDTO: PlayerDTO): InvFrame {
        return InvFX.frame(6, text("${playerDTO.playerEntity.name}의 한정퀘스트 목록")) {
            slot(8, 5) {
                item = returnIcon
                onClick { playerDTO.playerEntity.openFrame(getQuestMainFx(playerDTO)) }
            }
        }
    }
}