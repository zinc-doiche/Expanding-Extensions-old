package com.github.zinc;

import com.github.zinc.database.TaskManager
import com.github.zinc.player.listener.PlayerStatusListener
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin;

class ZincPlugin: JavaPlugin() {

    override fun onEnable() {
        plugin = this
        register(TaskManager())
        register(PlayerStatusListener())
    }

    override fun onDisable() {

    }

    private fun register(listener: Listener) {
        plugin.server.pluginManager.registerEvents(listener, this)
    }
}

internal lateinit var plugin: JavaPlugin
internal fun info(msg: Any) { plugin.logger.info(msg.toString()) }
internal fun warn(msg: Any) { plugin.logger.warning(msg.toString()) }