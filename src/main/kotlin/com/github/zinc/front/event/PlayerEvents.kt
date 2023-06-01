package com.github.zinc.front.event

import com.github.zinc.core.equipment.ZincEquipment
import org.bukkit.entity.Enemy
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.inventory.ItemStack

class PlayerGetExpEvent(player: Player, val amount: Int): PlayerEventAdapter(player)

class PlayerUseToolEvent(
    user: Player,
    val mainItem: ItemStack,
    val offItem: ItemStack
): PlayerEventAdapter(user), Cancellable {
    private var cancelled: Boolean = false
    override fun isCancelled() = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }
}

class PlayerEquipmentChangeEvent(playerEntity: Player, equipment: ZincEquipment) : PlayerEventAdapter(playerEntity)