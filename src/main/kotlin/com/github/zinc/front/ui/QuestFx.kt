package com.github.zinc.front.ui

import com.github.zinc.core.player.PlayerData
import com.github.zinc.core.quest.QuestDAO
import com.github.zinc.core.quest.QuestDTO
import com.github.zinc.core.quest.QuestVO
import com.github.zinc.core.quest.QuestManager
import com.github.zinc.util.Colors
import com.github.zinc.util.extension.getCustomItem
import com.github.zinc.util.extension.item
import com.github.zinc.util.extension.text
import com.github.zinc.util.extension.texts
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component
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

    fun getQuestMainFx(playerData: PlayerData): InvFrame {
        val manager = playerData.manager!!
        return InvFX.frame(1, text("${manager.playerEntity.name}의 임무")) {
            slot(0, 0) {
                item = getHeadIcon(manager.playerEntity)
                onClick {
                    val list = QuestDAO().use { dao ->
                        dao.selectList(playerData.playerVO.playerId)
                            ?.filter { it.questType == "daily" } ?: return@onClick
                    }
                    getQuestListFx(playerData, text("일일 임무"), list).let {
                        manager.playerEntity.openFrame(it)
                    }
                }
            }
            slot(1, 0) {
                item = witherIcon
                onClick {
                    val list = QuestDAO().use { dao ->
                        dao.selectList(playerData.playerVO.playerId)
                            ?.filter { it.questType == "weekend" } ?: return@onClick
                    }
                    getQuestListFx(playerData, text("주간 임무"), list).let{
                        manager.playerEntity.openFrame(it)
                    }
                }
            }
            slot(2, 0) {
                item = creeperIcon
                onClick {
                    val list = QuestDAO().use { dao ->
                        dao.selectList(playerData.playerVO.playerId)
                            ?.filter { it.questType == "limit" } ?: return@onClick
                    }
                    getQuestListFx(playerData, text("일일 특별 임무"), list).let {
                        manager.playerEntity.openFrame(it)
                    }
                }
            }
        }
    }

    private fun getQuestListFx(playerData: PlayerData, name: Component, questList: List<QuestDTO>): InvFrame {
        val cleared = questList.filter(QuestDTO::appendedQuestCleared)
        val unCleared = questList.filterNot(QuestDTO::appendedQuestCleared)

        return InvFX.frame(6, name) {
            list(0, 0, 8, 1, true, { cleared }) {
                transform(QuestFx::getQuestIcon)
            }
            pane(0, 2, 8, 2) { for(i in 0..8) item(i, 0, woodenPane) }
            list(0, 3, 8, 4, true, { unCleared }) {
                transform{ quest ->
                    if(quest.questType == "limit") getQuestIcon(quest).apply {
                        editMeta { meta ->
                            meta.lore()?.addAll(texts(
                                text(""),
                                text("Shift Click으로 새 임무를 받습니다").color(Colors.gold)
                            )) ?: return@editMeta
                        }
                    } else getQuestIcon(quest)
                }

                //임무 리롤 이벤트
                onClickItem { _, _, data, event ->
                    if(!event.isShiftClick || data.first.questType != "limit")  return@onClickItem
                    var newQuestName: String
                    val questVO: QuestVO

                    QuestDAO().use { dao ->
                        var chance = dao.getChance(data.first.appendedPlayerId)
                        if(chance <= 0) return@onClickItem

                        dao.updateChance(data.first.appendedPlayerId, --chance)

                        newQuestName = QuestManager.randomQuests.keys
                                .minus(questList.map { it.appendedQuestName }.toSet())
                                .random()
                        dao.updateLimited(newQuestName, data.first.appendedQuestId)

                        questVO = dao.selectQuest(newQuestName)
                    }

                    val newQuestList = questList.map { quest ->
                        if(quest.appendedQuestId == data.first.appendedQuestId)
                            quest.apply(questVO)
                        else
                            quest
                    }

                    getQuestListFx(playerData, text("일일 특별 임무"), newQuestList).let {
                        playerData.manager!!.playerEntity.openFrame(it)
                    }
                }
            }
            pane(0, 5, 7, 5) { for(i in 0..7) item(i, 0, woodenPane) }
            slot(8, 5) {
                item = returnIcon
                onClick {
                    playerData.manager!!.playerEntity.openFrame(getQuestMainFx(playerData))
                }
            }
        }
    }
}