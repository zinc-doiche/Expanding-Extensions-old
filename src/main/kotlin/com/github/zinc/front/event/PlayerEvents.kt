package com.github.zinc.front.event

import com.github.zinc.core.equipment.Equipment
import com.github.zinc.core.equipment.ZincEquipment
import org.bukkit.entity.Enemy
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class PlayerGetExpEvent(player: Player, val amount: Int): PlayerEventAdapter(player)

class PlayerEquipEvent(playerEntity: Player, val equipment: Equipment, val equipSlot: EquipmentSlot) : PlayerEventAdapter(playerEntity, true)
