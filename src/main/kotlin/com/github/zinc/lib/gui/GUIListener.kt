package com.github.zinc.lib.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUIListener: Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if(event.inventory.holder is GUI) {
            (event.inventory.holder as GUI).onEvent(event, EventType.CLICK)
        }
    }

    @EventHandler
    fun onDrag(event: InventoryClickEvent) {
        if(event.inventory.holder is GUI) {
            (event.inventory.holder as GUI).onEvent(event, EventType.DRAG)
        }
    }

    @EventHandler
    fun onClose(event: InventoryClickEvent) {
        if(event.inventory.holder is GUI) {
            (event.inventory.holder as GUI).onEvent(event, EventType.CLOSE)
        }
    }

    @EventHandler
    fun onOpen(event: InventoryClickEvent) {
        if(event.inventory.holder is GUI) {
            (event.inventory.holder as GUI).onEvent(event, EventType.OPEN)
        }
    }

    @EventHandler
    fun onOther(event: InventoryClickEvent) {
        if(event.inventory.holder is GUI) {
            (event.inventory.holder as GUI).onEvent(event, EventType.OTHER)
        }
    }
}