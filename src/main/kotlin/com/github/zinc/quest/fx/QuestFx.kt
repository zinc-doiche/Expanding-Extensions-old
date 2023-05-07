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
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object QuestFx {
    private val witherIcon = item(Material.WITHER_SKELETON_SKULL, text("주간 임무 목록 바로가기").color(Colors.gold))
    private val creeperIcon = item(Material.CREEPER_HEAD, text("일일 특별 임무 목록 바로가기").color(Colors.skyblue))
    private val returnIcon = getCustomItem(Material.PAPER, text("돌아가기"), 3)
    private val woodenPane = getCustomItem(Material.PAPER, text(""), 2)

    private fun getHeadIcon(player: Player) = item(Material.PLAYER_HEAD) { meta ->
        (meta as SkullMeta).owningPlayer = player
        meta.displayName(text("일일 임무 목록 바로가기").color(Colors.skyblue))
    }

    private fun getQuestIcon(questDTO: QuestDTO): ItemStack {
        val color = if(questDTO.appendedQuestCleared) Colors.green else Colors.red
        return item(Material.BOOK, text("임무: ${questDTO.appendedQuestName}").color(color)) { meta ->
            if(questDTO.appendedQuestCleared) {
                meta.lore(texts(
                    text(""),
                    text("수령함 : ${questDTO.questReward} XP").color(Colors.green)
                ))
                meta.addEnchant(Enchantment.MENDING, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            } else {
                meta.lore(texts(
                    text(""),
                    text("현황 : ${questDTO.appendedQuestProgress} / ${questDTO.questRequire}").color(Colors.skyblue),
                    text("임무 리워드 : ${questDTO.questReward} XP").color(Colors.skyblue)
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
                    playerDTO.playerEntity.openFrame(getQuestListFx(playerDTO, text("일일 임무"), list))
                }
            }
            slot(1, 0) {
                item = witherIcon
                onClick {
                    val list = QuestDAO().use { dao ->
                        dao.selectList(playerDTO.playerId)?.filter { it.questType == "weekend" } ?: return@onClick
                    }
                    playerDTO.playerEntity.openFrame(getQuestListFx(playerDTO, text("주간 임무"), list))
                }
            }
            slot(2, 0) {
                item = creeperIcon
                onClick {
                    val list = QuestDAO().use { dao ->
                        dao.selectList(playerDTO.playerId)?.filter { it.questType == "limit" } ?: return@onClick
                    }
                    playerDTO.playerEntity.openFrame(getQuestListFx(playerDTO, text("일일 특별 임무"), list))
                }
            }
        }
    }

    private fun getQuestListFx(playerDTO: PlayerDTO, name: Component, questList: List<QuestDTO>): InvFrame {
        return InvFX.frame(6, name) {
            list(0, 0, 8, 1, true, {questList.filter(QuestDTO::appendedQuestCleared)}) {
                transform(QuestFx::getQuestIcon)
            }
            pane(0, 2, 8, 2) { for(i in 0..8) item(i, 0, woodenPane) }
            list(0, 3, 8, 4, true, {questList.filterNot(QuestDTO::appendedQuestCleared)}) {
                transform(QuestFx::getQuestIcon)
            }
            pane(0, 5, 7, 5) { for(i in 0..7) item(i, 0, woodenPane) }
            slot(8, 5) {
                item = returnIcon
                onClick {
                    playerDTO.playerEntity.openFrame(getQuestMainFx(playerDTO))
                }
            }
        }
    }
}