package com.github.zinc.front.ui

import com.github.zinc.core.quest.QuestDTO
import com.github.zinc.lib.constant.Colors
import com.github.zinc.lib.gui.EventType
import com.github.zinc.lib.gui.SquareGUI
import com.github.zinc.module.user.`object`.User
import com.github.zinc.util.getCustomItem
import com.github.zinc.util.getSkull
import com.github.zinc.util.item
import com.github.zinc.util.text
import com.github.zinc.util.list
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class QuestGUI(private val uuid: String) : SquareGUI() {
    private val returnIcon = getCustomItem(Material.PAPER, text("돌아가기"), 3)
    private val woodenPane = getCustomItem(Material.PAPER, text(""), 2)

    private val main: Inventory = Bukkit.createInventory(this, 9, text("${User[uuid]?.name}의 임무"))
    private val list: Inventory = Bukkit.createInventory(this, 6 * 9, text("임무 목록"))

    override fun getInventory(): Inventory {
        return if(main.viewers.size == 0) list else main
    }

    private fun getHeadIcon(player: Player) = getSkull(player) { meta ->
        meta.displayName(text("일일 임무 목록 바로가기").color(Colors.skyblue))
    }

    private fun getQuestIcon(questDTO: QuestDTO): ItemStack {
        val color = if(questDTO.appendedQuestCleared) NamedTextColor.GREEN else NamedTextColor.RED
        return item(Material.BOOK, text("임무: ${questDTO.appendedQuestName}").color(color)) { meta ->
            if(questDTO.appendedQuestCleared) {
                meta.lore(
                    list(
                    text(""),
                    text("수령함 : ${questDTO.questReward} XP").color(NamedTextColor.GREEN)
                )
                )
                meta.addEnchant(Enchantment.MENDING, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            } else {
                meta.lore(
                    list(
                    text(""),
                    text("현황 : ${questDTO.appendedQuestProgress} / ${questDTO.questRequire}").color(Colors.skyblue),
                    text("임무 리워드 : ${questDTO.questReward} XP").color(Colors.skyblue)
                )
                )
            }
        }
    }

    fun openMain() {
        val player = Bukkit.getPlayer(uuid) ?: return
        main.clear()
        setItem(getHeadIcon(player), 0, 0)
//                onClick {
//                    val list = QuestDAO().use { dao ->
//                        dao.selectList(playerData.playerVO.playerId)
//                            ?.filter { it.questType == "daily" } ?: return@onClick
//                    }
//                    getQuestListFx(playerData, text("일일 임무"), list).let {
//                        manager.playerEntity.openFrame(it)
//                    }
//                }
        setItem(item(Material.WITHER_SKELETON_SKULL, text("주간 임무 목록 바로가기").color(NamedTextColor.GOLD)), 1, 0)
//                onClick {
//                    val list = QuestDAO().use { dao ->
//                        dao.selectList(playerData.playerVO.playerId)
//                            ?.filter { it.questType == "weekend" } ?: return@onClick
//                    }
//                    getQuestListFx(playerData, text("주간 임무"), list).let{
//                        manager.playerEntity.openFrame(it)
//                    }
//                }
        setItem(item(Material.CREEPER_HEAD, text("일일 특별 임무 목록 바로가기").color(Colors.skyblue)), 2, 0)
//        onClick {
//            val list = QuestDAO().use { dao ->
//                dao.selectList(playerData.playerVO.playerId)
//                    ?.filter { it.questType == "limit" } ?: return@onClick
//            }
//            getQuestListFx(playerData, text("일일 특별 임무"), list).let {
//                manager.playerEntity.openFrame(it)
//            }
//        }
    }

    private fun openList() {
//        val cleared = questList.filter(QuestDTO::appendedQuestCleared)
//        val unCleared = questList.filterNot(QuestDTO::appendedQuestCleared)
//
//        for (i in 0..17) {
//
//        }
//            list(0, 0, 8, 1, true, { cleared }) {
//                transform(QuestFx::getQuestIcon)
//            }
//            pane(0, 2, 8, 2) { for(i in 0..8) item(i, 0, woodenPane) }
//            list(0, 3, 8, 4, true, { unCleared }) {
//                transform{ quest ->
//                    if(quest.questType == "limit") getQuestIcon(quest).apply {
//                        editMeta { meta ->
//                            meta.lore()?.addAll(
//                                texts(
//                                text(""),
//                                text("Shift Click으로 새 임무를 받습니다").color(Colors.gold)
//                            )
//                            ) ?: return@editMeta
//                        }
//                    } else getQuestIcon(quest)
//                }
//
//                //임무 리롤 이벤트
//                onClickItem { _, _, data, event ->
//                    if(!event.isShiftClick || data.first.questType != "limit")  return@onClickItem
//                    var newQuestName: String
//                    val questVO: QuestVO
//
//                    QuestDAO().use { dao ->
//                        var chance = dao.getChance(data.first.appendedPlayerId)
//                        if(chance <= 0) return@onClickItem
//
//                        dao.updateChance(data.first.appendedPlayerId, --chance)
//
//                        newQuestName = QuestManager.randomQuests.keys
//                                .minus(questList.map { it.appendedQuestName }.toSet())
//                                .random()
//                        dao.updateLimited(newQuestName, data.first.appendedQuestId)
//
//                        questVO = dao.selectQuest(newQuestName)
//                    }
//
//                    val newQuestList = questList.map { quest ->
//                        if(quest.appendedQuestId == data.first.appendedQuestId)
//                            quest.apply(questVO)
//                        else
//                            quest
//                    }
//
//                    getQuestListFx(playerData, text("일일 특별 임무"), newQuestList).let {
//                        playerData.manager!!.playerEntity.openFrame(it)
//                    }
//                }
//            }
//            pane(0, 5, 7, 5) { for(i in 0..7) item(i, 0, woodenPane) }
//            slot(8, 5) {
//                item = returnIcon
//                onClick {
//                    playerData.manager!!.playerEntity.openFrame(getQuestMainFx(playerData))
//                }
//            }
//        }
    }

    override fun open() {
        openMain()
    }

    override fun onEvent(event: InventoryEvent, type: EventType) {
        when(type) {
            EventType.CLICK -> {

            }
            EventType.CLOSE -> {

            }
            else -> {

            }
        }
    }
}