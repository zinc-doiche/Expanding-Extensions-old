package com.github.zinc.quest.fx

import com.github.zinc.player.domain.PlayerDTO
import com.github.zinc.quest.dao.QuestDAO
import com.github.zinc.quest.domain.QuestDTO
import com.github.zinc.util.color.Colors
import com.github.zinc.util.extension.getCustomItem
import com.github.zinc.util.extension.item
import com.github.zinc.util.extension.text
import com.github.zinc.util.extension.texts
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.openFrame
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object QuestFx {
    private val creeperIcon = item(Material.CREEPER_HEAD, text("한정 임무 목록 바로가기").color(Colors.skyblue))
    private val returnIcon = getCustomItem(Material.PAPER, text("돌아가기"), 3)

    private fun getHeadIcon(player: Player) = item(Material.PLAYER_HEAD) { meta ->
        (meta as SkullMeta).owningPlayer = player
        meta.displayName(text("일일 임무 목록 바로가기").color(Colors.skyblue))
    }

    private fun getQuestIcon(questDTO: QuestDTO): ItemStack {
        val color = if(questDTO.appendedQuestCleared) Colors.red else Colors.green
        return getCustomItem(Material.PAPER, text("임무: ${questDTO.appendedQuestName}").color(color), 4) { meta ->
            meta.addEnchant(Enchantment.MENDING, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            if(questDTO.appendedQuestCleared) {
                meta.lore(texts(
                    text(""),
                    text("현황 : ${questDTO.appendedQuestProgress} / ${questDTO.questRequire}").color(Colors.skyblue),
                    text("임무 리워드 : ${questDTO.questReward} XP").color(Colors.skyblue)
                ))
            } else {
                meta.lore(texts(
                    text(""),
                    text("수령함 : ${questDTO.questReward} XP").color(Colors.green)
                ))
            }
        }
    }

    fun getQuestMainFx(playerDTO: PlayerDTO): InvFrame {
        return InvFX.frame(1, text("${playerDTO.playerEntity.name}의 임무")) {
            slot(0, 0) {
                item = getHeadIcon(playerDTO.playerEntity)
                onClick {
                    val list = QuestDAO().use { dao ->
                        dao.selectList(playerDTO.playerId)?.filter { it.questType == "daily" } ?: return@onClick
                    }
                    playerDTO.playerEntity.openFrame(getDailyQuestListFx(playerDTO, list))
                }
            }
            slot(1, 0) {
                item = creeperIcon
                onClick {
                    playerDTO.playerEntity.openFrame(getLimitedQuestListFx(playerDTO))
                }
            }
        }
    }

    private fun getDailyQuestListFx(playerDTO: PlayerDTO, questList: List<QuestDTO>): InvFrame {
        return InvFX.frame(5, text("일일 임무")) {
            slot(8, 4) {
                item = returnIcon
                onClick { playerDTO.playerEntity.openFrame(getQuestMainFx(playerDTO)) }
            }

            list(1, 0, 7, 2, true, {questList.filter(QuestDTO::appendedQuestCleared)}) {
                transform { quest ->
                    getQuestIcon(quest)
                }
                index = 0

            }

            list(4, 0, 7, 2, true, {questList.filterNot(QuestDTO::appendedQuestCleared)}) {
                transform { quest ->
                    getQuestIcon(quest)
                }
            }
        }
    }

    private fun getLimitedQuestListFx(playerDTO: PlayerDTO): InvFrame {
        return InvFX.frame(5, text("한정 임무")) {
            slot(8, 4) {
                item = returnIcon
                onClick { playerDTO.playerEntity.openFrame(getQuestMainFx(playerDTO)) }
            }
        }
    }
}