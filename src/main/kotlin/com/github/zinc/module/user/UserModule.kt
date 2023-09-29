package com.github.zinc.module.user

import com.github.zinc.module.Module
import com.github.zinc.module.user.listener.UserListener
import com.github.zinc.plugin

class UserModule: Module {
    override fun registerCommands() {

    }

    override fun registerListeners() {
        with(plugin.server.pluginManager) {
            registerEvents(UserListener(), plugin)
        }
    }
}