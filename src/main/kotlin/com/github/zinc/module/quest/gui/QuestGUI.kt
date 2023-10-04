package com.github.zinc.module.quest.gui

import com.github.zinc.lib.gui.EventType
import com.github.zinc.lib.gui.SquareGUI
import com.github.zinc.module.user.`object`.User
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory

class QuestGUI(val uuid: String): SquareGUI() {
    private val inventory = Bukkit.createInventory(this, 9 * 6, Component.empty())

    override fun open() {
        val user = User[uuid] ?: return



        user.player?.openInventory(inventory)
    }

    override fun onEvent(event: InventoryEvent, type: EventType) {
        when(type) {
            EventType.CLICK -> {

            }
            else -> {}
        }
    }

    override fun getInventory(): Inventory = inventory
}