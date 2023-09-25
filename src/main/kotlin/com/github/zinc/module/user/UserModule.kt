package com.github.zinc.module.user

import com.github.zinc.module.Module
import com.github.zinc.module.user.listener.UserListener
import com.github.zinc.plugin
import io.github.monun.kommand.kommand

class UserModule: Module {
    override fun registerCommands() {
//        plugin.kommand {
//            register("status") {
//                then("open") {
//
//                }
//            }
//        }
    }

    override fun registerListeners() {
        with(plugin.server.pluginManager) {
            registerEvents(UserListener(), plugin)
        }
    }
}