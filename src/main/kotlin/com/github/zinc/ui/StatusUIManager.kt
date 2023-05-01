package com.github.zinc.ui

import com.github.zinc.player.domain.PlayerDTO
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.InventoryView

class StatusUIManager(private val playerDTO: PlayerDTO) {
    fun openStatusUI() {
        playerDTO.playerEntity
    }

//    fun getInventory(): Inventory {
//
//    }

}