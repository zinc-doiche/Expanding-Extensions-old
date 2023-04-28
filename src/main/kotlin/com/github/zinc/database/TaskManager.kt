package com.github.zinc.database

import com.github.zinc.plugin
import com.github.zinc.util.scheduler.asyncLoop
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent

class TaskManager: Listener {

    @EventHandler
    fun onLoaded(e: ServerLoadEvent) {
        asyncLoop(0L, 20L * 60 * 5) {
            updatedTasks.forEach { it.value() }
        }
    }

    companion object {
        private val updatedTasks = HashMap<String, () -> Unit>()

        fun add(key: String, task: () -> Unit) { updatedTasks[key] = task }
        fun remove(key: String) { updatedTasks.remove(key) }
    }
}