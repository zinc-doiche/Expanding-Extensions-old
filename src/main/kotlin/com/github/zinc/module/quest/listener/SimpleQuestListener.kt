package com.github.zinc.module.quest.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class SimpleQuestListener: Listener {
    @EventHandler
    fun onDeath(event: EntityDeathEvent) {

    }
}