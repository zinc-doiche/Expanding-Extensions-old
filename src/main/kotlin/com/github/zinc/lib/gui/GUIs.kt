package com.github.zinc.lib.gui

import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

interface GUI: InventoryHolder {
    fun open()
    fun onEvent(event: InventoryEvent, type: EventType)
}

abstract class SquareGUI: GUI {
    fun setItem(item: ItemStack, x: Int, y: Int) {
        inventory.setItem(x + y * 9, item)
    }

    fun getItem(x: Int, y: Int): ItemStack? {
        return inventory.getItem(x + y * 9)
    }
}

enum class EventType {
    CLICK,
    DRAG,
    CLOSE,
    OPEN,
    OTHER
}