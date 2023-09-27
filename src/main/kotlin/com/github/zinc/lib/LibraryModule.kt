package com.github.zinc.lib

import com.github.zinc.lib.gui.GUIListener
import com.github.zinc.module.Module
import com.github.zinc.plugin

class LibraryModule: Module {
    override fun registerCommands() {}

    override fun registerListeners() {
        plugin.server.pluginManager.registerEvents(GUIListener(), plugin)
    }
}