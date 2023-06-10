package com.github.zinc.front.listener

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import com.destroystokyo.paper.loottable.LootableInventoryReplenishEvent
import com.github.zinc.container.EquipmentContainer
import com.github.zinc.container.PlayerContainer
import com.github.zinc.core.equipment.STATUS_KEY
import com.github.zinc.core.equipment.ToolManager.isEquipment
import com.github.zinc.core.equipment.ZincEquipment
import com.github.zinc.core.equipment.isTool
import com.github.zinc.core.player.PlayerDAO
import com.github.zinc.core.player.PlayerData
import com.github.zinc.core.player.PlayerStatusManager
import com.github.zinc.core.quest.QuestDAO
import com.github.zinc.front.event.QuestClearEvent
import com.github.zinc.core.quest.QuestManager
import com.github.zinc.front.event.PlayerEquipEvent
import com.github.zinc.front.event.PlayerGetItemEvent
import com.github.zinc.front.event.PlayerUseToolEvent
import com.github.zinc.info
import com.github.zinc.util.ChainEventCall
import com.github.zinc.util.Sounds
import com.github.zinc.util.async
import com.github.zinc.util.extension.*
import com.github.zinc.util.extension.getPersistent
import com.github.zinc.util.extension.hasPersistent
import com.github.zinc.util.extension.isNullOrAir
import com.github.zinc.util.extension.text
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Enemy
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLevelChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerStatisticIncrementEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*
import kotlin.collections.ArrayList

class PlayerListener: Listener {

    private val slots: ArrayList<EquipmentSlot> = arrayListOf(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD)

    @EventHandler
    fun onLogin(e: AsyncPlayerPreLoginEvent) {
        var isNewbie = false
        val playerName = e.playerProfile.name ?: return
        val playerVO = PlayerDAO().use { dao ->
            dao.select(playerName) ?: run {
                isNewbie = true
                dao.insert(playerName)
                dao.select(playerName)
            } ?: run {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, text("Login Cancelled, please retry login."))
                return
            }
        }

        if(isNewbie) QuestManager.registerAllQuests(playerVO.playerId)
        PlayerContainer.add(playerName, PlayerData(playerVO))

        QuestManager.clearMap[e.playerProfile.name!!] = QuestDAO().use { dao ->
            val questList = dao.selectList(playerVO.playerId) ?: return
            questList.filter { it.appendedQuestCleared }.map { it.appendedQuestName }.toHashSet()
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val playerData = PlayerContainer[e.player.name]!!
        playerData.manager = PlayerStatusManager(playerData, e.player)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val playerData = PlayerContainer.remove(e.player.name) ?: return
        PlayerDAO().use { it.update(playerData.playerVO) }

        QuestManager.clearMap.remove(e.player.name)
    }

    @EventHandler
    fun onRespawn(e: PlayerPostRespawnEvent) {
        PlayerContainer[e.player.name]!!.manager?.applyAll() ?: return
    }

    @EventHandler
    @ChainEventCall(PlayerUseToolEvent::class, QuestClearEvent::class)
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if(e.entity !is LivingEntity) return

        val player: Player = when(e.damager) {
            is Player -> e.damager as Player
            is AbstractArrow -> {
                val abstractArrow = e.damager as AbstractArrow
                val shooter = abstractArrow.shooter ?: return
                if(shooter !is Player) return
                shooter
            }
            else -> return
        }
        val playerData = PlayerContainer[player.name]!!

        if(!PlayerUseToolEvent(player, player.inventory.itemInMainHand, player.inventory.itemInOffHand).callEvent()) return

        val manager = playerData.manager ?: return
        e.damage = if(manager.rollCritical()) {
            player.playSound(Sounds.ironGolemDamaged)
            e.damage * 1.8
        } else e.damage

        player.sendMessage(text("${e.finalDamage}"))

        if(e.entity !is Enemy) return
        val enemy = e.entity as Enemy
        if(enemy.health > e.finalDamage) return

        if(QuestManager.clearMap[player.name]!!.contains(enemy.name)) {
            player.sendMessage("§6이미 ${enemy.name} 퀘스트를 완료하였습니다. 퀘스트는 매일 오전 2시에 초기화됩니다.")
            return
        }
        async { QuestClearEvent(playerData, enemy).callEvent() }
    }

    @EventHandler
    @ChainEventCall(PlayerEquipEvent::class, PlayerGetItemEvent::class)
    fun onInvSlotChanged(e: PlayerInventorySlotChangeEvent) {
        async{
            e.player.inventory.getItem(e.slot)?.let { item ->
                if(isNullOrAir(item)) return@async

                return@let if(item.hasPersistent(STATUS_KEY)) EquipmentContainer[item.getPersistent(STATUS_KEY)!!]
                else {
                    UUID.randomUUID().let { uuid ->
                        item.setPersistent(STATUS_KEY, uuid.toString())

                        ZincEquipment(item).apply {
                            setStatus()
                            setPDC()
                            setLore()
                            EquipmentContainer[uuid.toString()] = this
                        }
                    }
                }
            }?.let { equipment ->
                if(e.slot in 36..39)
                    PlayerEquipEvent(e.player, equipment, slots[e.slot - 36]).callEvent()
            }
        }
    }

    @EventHandler
    fun onIncrement(e: PlayerStatisticIncrementEvent) {

    }

    @EventHandler
    fun onLevelUp(e: PlayerLevelChangeEvent) {

    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        when(e.action) {
            Action.LEFT_CLICK_BLOCK -> return
            Action.RIGHT_CLICK_BLOCK -> return
            Action.LEFT_CLICK_AIR -> return
            Action.RIGHT_CLICK_AIR -> return
            Action.PHYSICAL -> return
        }
    }
}

