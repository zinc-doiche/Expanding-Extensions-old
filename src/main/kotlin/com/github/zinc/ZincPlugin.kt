package com.github.zinc;

import org.bukkit.plugin.java.JavaPlugin;

class ZincPlugin: JavaPlugin() {

    override fun onEnable() {
        plugin = this
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}

internal lateinit var plugin: JavaPlugin

internal fun info(msg: Any) {
    plugin.logger.info(msg.toString())
}

internal fun warn(msg: Any) {
    plugin.logger.warning(msg.toString())
}