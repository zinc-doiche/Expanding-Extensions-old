package com.github.zinc.front.listener

import com.github.zinc.util.loop
import com.github.zinc.warn
//import io.github.monun.kommand.kommand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent

class ServerListener: Listener {

    @EventHandler
    fun onLoaded(e: ServerLoadEvent) {
        loop(0L, 20L * 60 * 10) {
            if (updatedTasks.isNotEmpty()) updatedTasks.forEach { it.value() }
        }
    }

    companion object {
        private val updatedTasks = HashMap<String, () -> Unit>()

        fun execute(key: String) = updatedTasks[key]?.invoke() ?: warn("the task \'$key\' is not registered.")

        fun add(key: String, task: () -> Unit) { updatedTasks[key] = task }
        fun remove(key: String) { updatedTasks.remove(key) }
    }
}