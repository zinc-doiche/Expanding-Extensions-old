package com.github.zinc.front.listener

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import com.github.zinc.container.EquipmentContainer
import com.github.zinc.container.PlayerContainer
import com.github.zinc.core.equipment.*
import com.github.zinc.core.equipment.isTool
import com.github.zinc.core.player.PlayerDAO
import com.github.zinc.core.player.PlayerData
import com.github.zinc.core.player.PlayerStatusManager
import com.github.zinc.core.quest.QuestDAO
import com.github.zinc.core.quest.QuestManager
import com.github.zinc.core.recipe.DynamicRecipe
import com.github.zinc.core.recipe.Recipes
import com.github.zinc.front.event.*
import com.github.zinc.info
import com.github.zinc.util.ChainEventCall
import com.github.zinc.util.Interaction
import com.github.zinc.util.Sounds
import com.github.zinc.util.async
import com.github.zinc.util.*
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import org.bukkit.Material
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Enemy
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLevelChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerStatisticIncrementEvent
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
    @ChainEventCall(QuestClearEvent::class)
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if(e.entity !is LivingEntity) return

        val player: Player =
            when(e.damager) {
                is Player -> e.damager as Player
                is AbstractArrow -> {
                    val abstractArrow = e.damager as AbstractArrow
                    val shooter = abstractArrow.shooter ?: return
                    if(shooter !is Player) return
                    shooter
                }
                //check if player is blocking when player cant use the shield
                else -> {
                    if(e.entity is Player && (e.entity as Player).isBlocking) {
                        val playerData = PlayerContainer[(e.entity as Player).name] ?: return
                        val player = playerData.manager?.playerEntity ?: return

                        val uuid =
                            if(player.inventory.itemInOffHand.type == Material.SHIELD)
                                player.inventory.itemInOffHand.getPersistent(STATUS_KEY)
                            else if(player.inventory.itemInMainHand.type == Material.SHIELD)
                                player.inventory.itemInMainHand.getPersistent(STATUS_KEY)
                            else return

                        val equipment = EquipmentContainer[uuid ?: return] ?: return

                        if(!equipment.isDeserved(playerData)) {
                            player.sendMessage("아직 사용하기엔 이르다.")
                            player.playSound(Sounds.ironGolemDamaged)
                            player.damage(e.damage, e.damager)
                        }
                    }
                return
                }
            }
        val playerData = PlayerContainer[player.name]!!

        //checks if player can use the equipment
        run {
            ArrayList<String>().apply {
                if(player.inventory.itemInMainHand.hasPersistent(STATUS_KEY))
                    add(player.inventory.itemInMainHand.getPersistent(STATUS_KEY)!!)

                if(player.inventory.itemInOffHand.hasPersistent(STATUS_KEY))
                    add(player.inventory.itemInOffHand.getPersistent(STATUS_KEY)!!)
            }.let { uuids ->
                if(uuids.isNotEmpty() && uuids
                        .map { EquipmentContainer[it] ?: return@run }
                        .all { it.isDeserved(playerData) }.not()
                ) {
                    player.sendMessage("아직 사용하기엔 이르다.")
                    e.isCancelled = true
                    return
                }
            }
        }

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
    @ChainEventCall(PlayerEquipEvent::class, EquipmentUpdateEvent::class)
    fun onInvSlotChanged(e: PlayerInventorySlotChangeEvent) {
        e.player.inventory.getItem(e.slot)?.let { item ->
            if(isNullOrAir(item) || !item.isTool()) return
            return@let if(item.hasPersistent(STATUS_KEY)) {
                val uuid = item.getPersistent(STATUS_KEY)!!
                // will be extended with a map (namespaced key, ? extends ZincEq)
                if(item.hasPersistent(OceanArmor.KEY)) EquipmentContainer[uuid] = OceanArmor(item)
                EquipmentContainer[uuid]?.let { equipment ->
                    //needs the custom overrode equals of itemMeta
                    if(isNullOrAir(equipment.item) || equipment.item.itemMeta != item.itemMeta) {
                        equipment.item = item
                        info("updated ${item.type.name}")
                        EquipmentUpdateEvent(equipment).callEvent()
                    }
                    equipment
                } ?: Equipment.register(uuid, ZincEquipment(item))
            }
            else {
                UUID.randomUUID().let { uuid ->
                    item.setPersistent(STATUS_KEY, uuid.toString())

                    Equipment.register(uuid.toString(), ZincEquipment(item)).apply {
                        info("init new $uuid to ${item.type} with $status")
                    }
                }
            }
        }?.let { equipment ->
            if(e.slot in 36..39)
                async { PlayerEquipEvent(e.player, equipment, slots[e.slot - 36]).callEvent() }
        }
    }

    /**
     * 1. 직접 인챈트시
     * 2. 모루 사용시
     * 3. 숫돌로 인챈트 롤백시
     * 4. 도구 조합으로 인첸트 롤백시
     */
    @EventHandler
    fun onChangeEnchant(e: ItemChangeEnchantEvent) {
        if(!e.item.hasPersistent(STATUS_KEY)) return
    }

    @EventHandler
    fun onIncrement(e: PlayerStatisticIncrementEvent) {

    }

    @EventHandler
    fun onLevelUp(e: PlayerLevelChangeEvent) {

    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        when (e.action) {
            Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR -> return
            Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR -> return
            Action.PHYSICAL -> return
        }
    }

    @EventHandler
    fun onInvClick(e: InventoryClickEvent) {
        if(e.inventory != e.view.topInventory) return

        when(e.inventory.type) {
            InventoryType.SMITHING -> {
                if(e.rawSlot != 2 || isNullOrAir(e.currentItem) || !isNullOrAir(e.cursor)) return
                Interaction.doInteraction(Interaction.GET, e.view, e.rawSlot, e.isShiftClick)
                e.inventory.setItem(0, AIR)
                e.inventory.getItem(1)?.subtract() ?: return
            }
            else -> return
        }
    }

    @EventHandler
    fun onPrepare(e: PrepareSmithingEvent) {
        val origin = e.inventory.getItem(0) ?: return
        val ingredient = e.inventory.getItem(1) ?: return

        for (recipe in Recipes.customRecipes) {
            if(recipe.isCorrect(origin, ingredient)) {
                e.result = recipe.getResult(origin).apply {
                    if(hasPersistent(DynamicRecipe.dynamicKey)) editMeta { it.displayName(displayName()) }
                }
                break
            }
        }
    }

    @EventHandler
    fun onItemRemove(e: EntityRemoveFromWorldEvent) {
        if(e.entity !is Item) return
        val entity = e.entity as Item
        if(!entity.itemStack.hasPersistent(STATUS_KEY)) return
        EquipmentContainer.container.remove(entity.itemStack.getPersistent(STATUS_KEY))
    }
}

