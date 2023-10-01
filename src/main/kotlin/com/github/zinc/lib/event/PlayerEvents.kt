package com.github.zinc.lib.event

import com.github.zinc.module.item.`object`.equipment.Equipment
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

class PlayerGetExpEvent(player: Player, val amount: Int): PlayerEventAdapter(player)

class PlayerEquipEvent(playerEntity: Player, val equipment: Equipment, val equipSlot: EquipmentSlot) : PlayerEventAdapter(playerEntity, true)
