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
        plugin.kommand {
            register("trinket", "장신구", "트링켓") {
                then("open") {
                    executes {
                        TrinketGUI(player.uniqueId.toString()).open()
                    }
                }
                then("get", "name" to string()) {
                    requires { player.isOp }
                    executes {
                        val name: String by it
                        val trinket = Trinket[name] ?: run {
                            player.warn("존재하지 않는 장신구입니다.")
                            return@executes
                        }
                        val item = trinket.item ?: return@executes
                        player.addItem(item)
                    }
                }
            }
        }
    }

    override fun registerListeners() {
        with(plugin.server.pluginManager) {
            registerEvents(UserListener(), plugin)
        }
    }
}