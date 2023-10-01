package com.github.zinc.lib.event

import com.github.zinc.module.item.`object`.equipment.Equipment
import com.github.zinc.module.user.`object`.User
import org.bukkit.entity.Enemy
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class QuestClearEvent(val player: Player, val enemy: Enemy) : EventAdapter(true)
class ItemChangeEnchantEvent(val item: ItemStack) : EventAdapter()
class EquipmentUpdateEvent(val equipment: Equipment) : EventAdapter()

class AsyncUserLevelUpEvent(val user: User): EventAdapter(true)