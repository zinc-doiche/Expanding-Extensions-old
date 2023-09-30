package com.github.zinc.lib.event

import com.github.zinc.core.equipment.Equipment
import com.github.zinc.core.player.PlayerData
import com.github.zinc.module.user.`object`.User
import org.bukkit.entity.Enemy
import org.bukkit.inventory.ItemStack

class QuestClearEvent(val playerData: PlayerData, val enemy: Enemy) : EventAdapter(true)
class ItemChangeEnchantEvent(val item: ItemStack) : EventAdapter()
class EquipmentUpdateEvent(val equipment: Equipment) : EventAdapter()

class AsyncUserLevelUpEvent(val user: User): EventAdapter(true)