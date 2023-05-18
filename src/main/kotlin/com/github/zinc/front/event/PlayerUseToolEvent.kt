package com.github.zinc.front.event

import com.github.zinc.util.PlayerEventAdapter
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

class PlayerUseToolEvent(
    val damageEvent: EntityDamageByEntityEvent,
    user: Player,
    val mainItem: ItemStack,
    val offItem: ItemStack
): PlayerEventAdapter(user)