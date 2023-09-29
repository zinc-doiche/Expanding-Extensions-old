package com.github.zinc.module.user

import com.github.zinc.module.Module
import com.github.zinc.module.item.`object`.trinket.Trinket
import com.github.zinc.module.user.gui.TrinketGUI
import com.github.zinc.module.user.listener.UserListener
import com.github.zinc.plugin
import com.github.zinc.util.addItem
import com.github.zinc.util.warn
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand

class UserModule: Module {
    override fun registerCommands() {

    }

    override fun registerListeners() {
        with(plugin.server.pluginManager) {
            registerEvents(UserListener(), plugin)
        }
    }
}