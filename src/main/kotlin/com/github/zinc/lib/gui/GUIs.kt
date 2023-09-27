package com.github.zinc.lib.gui

import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

interface GUI: InventoryHolder {
    fun open()
    fun onEvent(event: InventoryEvent, type: EventType)

    fun setItem(item: ItemStack?, slot: Int) {
        inventory.setItem(slot, item)
    }

    fun getItem(slot: Int): ItemStack? {
        return inventory.getItem(slot)
    }
}

abstract class SquareGUI: GUI {
    fun setItem(item: ItemStack?, x: Int, y: Int) {
        setItem(item, x + y * 9)
    }

    fun getItem(x: Int, y: Int): ItemStack? {
        return getItem(x + y * 9)
    }
}

internal fun Inventory.setItem(item: ItemStack?, x: Int, y: Int) = setItem(x + y * 9, item)

enum class EventType {
    CLICK,
    DRAG,
    CLOSE,
    OPEN
}