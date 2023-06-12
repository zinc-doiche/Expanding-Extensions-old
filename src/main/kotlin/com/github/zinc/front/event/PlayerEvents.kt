package com.github.zinc.front.event

import com.github.zinc.core.equipment.ZincEquipment
import org.bukkit.entity.Enemy
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class PlayerGetExpEvent(player: Player, val amount: Int): PlayerEventAdapter(player)

class PlayerUseToolEvent(
    user: Player,
    val usingItem: ZincEquipment,
): PlayerEventAdapter(user, true), Cancellable {
    private var cancelled: Boolean = false
    override fun isCancelled() = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }
}

class PlayerEquipEvent(playerEntity: Player, val equipment: ZincEquipment, val equipSlot: EquipmentSlot) : PlayerEventAdapter(playerEntity, true)
class PlayerGetItemEvent(playerEntity: Player, val equipment: ZincEquipment) : PlayerEventAdapter(playerEntity, true)