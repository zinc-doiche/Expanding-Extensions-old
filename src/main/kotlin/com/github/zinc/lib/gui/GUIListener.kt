package com.github.zinc.lib.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent

class GUIListener: Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if(event.inventory.holder is GUI) {
            (event.inventory.holder as GUI).onEvent(event, EventType.CLICK)
        }
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        if(event.inventory.holder is GUI) {
            (event.inventory.holder as GUI).onEvent(event, EventType.DRAG)
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if(event.inventory.holder is GUI) {
            (event.inventory.holder as GUI).onEvent(event, EventType.CLOSE)
        }
    }

    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        if(event.inventory.holder is GUI) {
            (event.inventory.holder as GUI).onEvent(event, EventType.OPEN)
        }
    }
}