package com.github.zinc.front.listener

import com.github.zinc.container.PlayerContainer
import com.github.zinc.core.equipment.ToolManager
import com.github.zinc.core.equipment.ToolManager.BALANCE
import com.github.zinc.core.equipment.ToolManager.CONCENTRATION
import com.github.zinc.core.equipment.ToolManager.STRENGTH
import com.github.zinc.core.equipment.ToolManager.SWIFTNESS
import com.github.zinc.core.equipment.ToolManager.isTool
import com.github.zinc.core.player.StatusType
import com.github.zinc.front.event.PlayerUseToolEvent
import com.github.zinc.util.Colors
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
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * pdc를 추가해야 되는 상황:
 * 1. 상자서 꺼냄
 * 2. 조합
 * 3. 땅에떨어진거 먹음
 *
 * 검사해야 되는 상황:
 * 1. 인벤에서 입음
 * 2. 우클릭으로 입음
 *
 * 검사 후 실패시
 * 1. 인벤으로 리턴
 * 2. 꽉 차면 뱉는다.
 *
 * 칼같은 거는 사용하는 이벤트 캔슬 필요
 */
class ToolConstraintListener: Listener {
    @EventHandler
    fun onLooting(e: InventoryMoveItemEvent) {
        async {
            if(!e.item.hasPersistent(ToolManager.STATUS_KEY)) return@async
            e.item.setPersistent(ToolManager.STATUS_KEY, "status")

            setConstraints(e.item)
        }
    }

    @EventHandler
    fun onPickup(e: PlayerAttemptPickupItemEvent) {
        async {
            if(!e.item.itemStack.hasPersistent(ToolManager.STATUS_KEY) || !e.item.itemStack.isTool()) return@async
            e.item.itemStack.setPersistent(ToolManager.STATUS_KEY, "status")
            setConstraints(e.item.itemStack)
        }
    }

    @EventHandler
    fun onCraft(e: InventoryClickEvent) {
        (if(e.view.topInventory is CraftingInventory) e.view.topInventory as CraftingInventory else return).result
            ?.let { item ->
            async {
                if(!item.hasPersistent(ToolManager.STATUS_KEY) || !item.isTool()) return@async
                item.setPersistent(ToolManager.STATUS_KEY, "status")

                setConstraints(item)
            }
        } ?: return
    }

    @EventHandler
    fun onEquip(e: PlayerInventorySlotChangeEvent) {
        val playerData = PlayerContainer[e.player.name] ?: return
        e.player.inventory.getItem(e.slot)?.let { item ->
            if(isNullOrAir(item) || !item.hasPersistent(ToolManager.STATUS_KEY)) return

            val checkStr = item.getPersistent(STRENGTH, PersistentDataType.INTEGER)?.let { it < playerData.playerVO.playerStrength } ?: true
            val checkSwt = item.getPersistent(SWIFTNESS, PersistentDataType.INTEGER)?.let { it < playerData.playerVO.playerSwiftness } ?: true
            val checkBal = item.getPersistent(BALANCE, PersistentDataType.INTEGER)?.let { it < playerData.playerVO.playerBalance } ?: true
            val checkCon = item.getPersistent(CONCENTRATION, PersistentDataType.INTEGER)?.let { it < playerData.playerVO.playerConcentration } ?: true

            if(!(checkStr && checkSwt && checkBal && checkCon)) {
                e.player.sendMessage("아직 사용하기엔 이르다.")
                e.player.inventory.setItem(e.slot, AIR)
                if(e.player.inventory.addItem(item).isNotEmpty())
                    e.player.world.dropItem(e.player.location, item)
            }
        }
    }

    //passed by playerListener.onEntityDamage
    @EventHandler
    fun onUse(e: PlayerUseToolEvent) {

    }

    private fun setConstraints(itemStack: ItemStack) {
        val statusConstrains = ToolManager.getRequireStatus(itemStack).ifEmpty { return }
        val str = statusConstrains[StatusType.STRENGTH] ?: return
        val swt = statusConstrains[StatusType.SWIFTNESS] ?: return
        val bal = statusConstrains[StatusType.BALANCE] ?: return
        val con = statusConstrains[StatusType.CONCENTRATION] ?: return

        if(str > 0) itemStack.setPersistent(ToolManager.STRENGTH, str, PersistentDataType.INTEGER)
        if(swt > 0) itemStack.setPersistent(ToolManager.SWIFTNESS, swt, PersistentDataType.INTEGER)
        if(bal > 0) itemStack.setPersistent(ToolManager.BALANCE, bal, PersistentDataType.INTEGER)
        if(con > 0) itemStack.setPersistent(ToolManager.CONCENTRATION, con, PersistentDataType.INTEGER)

        itemStack.editMeta { meta ->
            meta.lore(texts(
                text(""),
                text("요구 스테이터스:", decoration = TextDecoration.BOLD).color(Colors.green),
                text("\tSTR").color(Colors.red).append(text(": $str", decoration = TextDecoration.BOLD).color(Colors.white)),
                text("\tSWT").color(Colors.skyblue).append(text(": $swt", decoration = TextDecoration.BOLD).color(Colors.white)),
                text("\tBAL").color(Colors.green).append(text(": $bal", decoration = TextDecoration.BOLD).color(Colors.white)),
                text("\tCON").color(Colors.gold).append(text(": $con", decoration = TextDecoration.BOLD).color(Colors.white))
            ))
        }
    }
}