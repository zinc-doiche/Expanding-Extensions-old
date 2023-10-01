package com.github.zinc.module.world

import com.github.zinc.module.Module
import com.github.zinc.module.world.listener.WorldListener
import com.github.zinc.plugin

class WorldModule: Module {
    override fun registerCommands() {

    }

    override fun registerListeners() {
        plugin.server.pluginManager.registerEvents(WorldListener(), plugin)
    }

    override fun onDisable() {

    }
}