package com.github.zinc;

import com.github.zinc.player.listener.PlayerStatusListener
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin;

class ZincPlugin: JavaPlugin() {

    override fun onEnable() {
        plugin = this

        registerAll(
            TaskManager(),
            PlayerStatusListener()
        )
    }

    override fun onDisable() {

    }

    private fun registerAll(vararg listener: Listener) {
        listener.forEach { plugin.server.pluginManager.registerEvents(it, this) }
    }
}

internal lateinit var plugin: JavaPlugin
internal fun info(msg: Any) { plugin.logger.info(msg.toString()) }
internal fun warn(msg: Any) { plugin.logger.warning(msg.toString()) }