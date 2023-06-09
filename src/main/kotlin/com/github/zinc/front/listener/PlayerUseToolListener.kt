package com.github.zinc.front.listener

import com.github.zinc.container.EquipmentContainer
import com.github.zinc.container.PlayerContainer
import com.github.zinc.core.equipment.ToolManager
import com.github.zinc.core.equipment.ToolManager.BALANCE
import com.github.zinc.core.equipment.ToolManager.CONCENTRATION
import com.github.zinc.core.equipment.ToolManager.STATUS_KEY
import com.github.zinc.core.equipment.ToolManager.STRENGTH
import com.github.zinc.core.equipment.ToolManager.SWIFTNESS
import com.github.zinc.core.equipment.ToolManager.isTool
import com.github.zinc.core.equipment.ZincEquipment
import com.github.zinc.core.player.StatusType
import com.github.zinc.front.event.PlayerEquipEvent
import com.github.zinc.front.event.PlayerGetItemEvent
import com.github.zinc.front.event.PlayerUseToolEvent
import com.github.zinc.info
import com.github.zinc.util.Colors
import com.github.zinc.util.PassedBy
import com.github.zinc.util.async
import com.github.zinc.util.extension.*
import com.github.zinc.util.extension.text
import com.github.zinc.util.extension.texts
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

/**
 * pdc를 추가해야 되는 상황:
 * 1. 상자서 꺼냄
 * 2. 조합
 * 3. 땅에떨어진거 먹음
 *
 * 검사해야 되는 상황:
 * 1. 인벤에서 입음
 * 2. 우클릭으로 입음
 * 3. 칼같은 거는 사용하는 이벤트 캔슬 필요
 *
 * 검사 후 실패시
 * 1. 인벤으로 리턴
 * 2. 꽉 차면 뱉는다.
 * 3. 캔슬
 */
class PlayerUseToolListener: Listener {
    @EventHandler
    fun onLooting(e: PlayerGetItemEvent) {

    }

    @EventHandler
    fun onPickup(e: PlayerAttemptPickupItemEvent) {
        info("P1")
        async {
            if(e.item.itemStack.hasPersistent(STATUS_KEY) || !e.item.itemStack.isTool()) return@async
            info("P2")
            e.item.itemStack.setPersistent(STATUS_KEY, "status")
            info(e.item.itemStack.getPersistent(STATUS_KEY) ?: return@async)
            e.item.itemStack.editMeta{ it.setConstraints(e.item.itemStack) }
        }
    }

    @EventHandler
    fun onCraft(e: InventoryClickEvent) {
        info("C1")
        async {
            val inv = if (e.view.topInventory is CraftingInventory) e.view.topInventory as CraftingInventory else return@async
            inv.result?.let { item ->
                if (item.hasPersistent(STATUS_KEY) || !item.isTool()) return@async
                info("C2")
                item.setPersistent(STATUS_KEY, "status")
                inv.result?.let { tool -> item.editMeta{ it.setConstraints(tool) } }
            } ?: return@async
        }
    }

    @EventHandler
    @PassedBy(PlayerListener::class, PlayerInventorySlotChangeEvent::class)
    fun onEquip(e: PlayerEquipEvent) {
        info("E1")
        async {
            val playerData = PlayerContainer[e.player.name] ?: return@async
            val item = e.equipment.equipment
            if(isNullOrAir(item) || item.hasPersistent(STATUS_KEY) || !item.isTool()) return@async
            info("E2")
            val checkStr = item.getPersistent(STRENGTH, PersistentDataType.INTEGER)?.let { it < playerData.playerVO.playerStrength } ?: true
            val checkSwt = item.getPersistent(SWIFTNESS, PersistentDataType.INTEGER)?.let { it < playerData.playerVO.playerSwiftness } ?: true
            val checkBal = item.getPersistent(BALANCE, PersistentDataType.INTEGER)?.let { it < playerData.playerVO.playerBalance } ?: true
            val checkCon = item.getPersistent(CONCENTRATION, PersistentDataType.INTEGER)?.let { it < playerData.playerVO.playerConcentration } ?: true

            if(!(checkStr && checkSwt && checkBal && checkCon)) {
                e.player.sendMessage("아직 사용하기엔 이르다.")
                e.player.inventory.setItem(e.equipSlot, AIR)
                if(e.player.inventory.addItem(item).isNotEmpty()) e.player.world.dropItem(e.player.location, item)
            }
        }
    }

    @EventHandler
    @PassedBy(PlayerListener::class, EntityDamageByEntityEvent::class)
    fun onUse(e: PlayerUseToolEvent) {
        info("U1")
        async {
            val playerData = PlayerContainer[e.player.name] ?: return@async
            val playerVO = playerData.playerVO
            val item = if(e.mainItem.hasPersistent(STATUS_KEY)) e.mainItem
            else if(e.offItem.hasPersistent(STATUS_KEY)) e.offItem else return@async

            info("U2")

            val checkStr = (item.getPersistent(STRENGTH, PersistentDataType.INTEGER) ?: 0) <= playerVO.playerStrength
            val checkSwt = (item.getPersistent(SWIFTNESS, PersistentDataType.INTEGER) ?: 0) <= playerVO.playerSwiftness
            val checkBal = (item.getPersistent(BALANCE, PersistentDataType.INTEGER) ?: 0) <= playerVO.playerBalance
            val checkCon = (item.getPersistent(CONCENTRATION, PersistentDataType.INTEGER) ?: 0) <= playerVO.playerConcentration

            if(checkStr && checkSwt && checkBal && checkCon) return@async
            e.isCancelled = true
        }
    }

    private fun ItemMeta.setConstraints(itemStack: ItemStack) {
        val statusConstrains = ToolManager.getRequireStatus(itemStack).ifEmpty { return }
        val str = statusConstrains[StatusType.STRENGTH] ?: 0
        val swt = statusConstrains[StatusType.SWIFTNESS] ?: 0
        val bal = statusConstrains[StatusType.BALANCE] ?: 0
        val con = statusConstrains[StatusType.CONCENTRATION] ?: 0

        info("$str $swt $bal $con")

        this.lore(texts(
            text(""),
            text("요구 스테이터스:", decoration = TextDecoration.BOLD).color(Colors.green)
        ).apply {
            if(str > 0) {
                itemStack.setPersistent(STRENGTH, str, PersistentDataType.INTEGER)
                this.add(text("\tSTR").color(Colors.red).append(text(": $str", decoration = TextDecoration.BOLD).color(Colors.white)))
            }
            if(swt > 0) {
                itemStack.setPersistent(SWIFTNESS, swt, PersistentDataType.INTEGER)
                this.add(text("\tSWT").color(Colors.skyblue).append(text(": $swt", decoration = TextDecoration.BOLD).color(Colors.white)))
            }
            if(bal > 0) {
                itemStack.setPersistent(BALANCE, bal, PersistentDataType.INTEGER)
                this.add(text("\tBAL").color(Colors.green).append(text(": $bal", decoration = TextDecoration.BOLD).color(Colors.white)))
            }
            if(con > 0) {
                itemStack.setPersistent(CONCENTRATION, con, PersistentDataType.INTEGER)
                this.add(text("\tCON").color(Colors.gold).append(text(": $con", decoration = TextDecoration.BOLD).color(Colors.white)))
            }
        })
    }
}