package com.github.zinc.module.item.listener

import com.github.zinc.module.item.`object`.OnHit
import com.github.zinc.module.item.`object`.OnHitDetection
import com.github.zinc.module.item.`object`.equipment.Equipment
import com.github.zinc.module.user.`object`.user
import com.github.zinc.util.getPersistent
import com.github.zinc.util.hasPersistent
import com.github.zinc.util.isNotNull
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerEvent

class ItemListener: Listener {
    @EventHandler
    fun onEquip(event: PlayerInventorySlotChangeEvent) {
        val player = event.player
        if(event.rawSlot !in 5..8) {
            return
        }
        if(event.newItemStack.hasPersistent(Equipment.namespace)) {
            val name = event.newItemStack.getPersistent(Equipment.namespace) ?: return
            val equipment = Equipment[name] ?: return
            equipment.onEquip(player)
        }
        if(event.oldItemStack.hasPersistent(Equipment.namespace)) {
            val name = event.oldItemStack.getPersistent(Equipment.namespace) ?: return
            val equipment = Equipment[name] ?: return
            equipment.onTakeOff(player)
        }
    }
}