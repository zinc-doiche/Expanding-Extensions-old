package com.github.zinc.module.item

import com.github.zinc.info
import com.github.zinc.module.Module
import com.github.zinc.module.item.listener.ItemListener
import com.github.zinc.module.item.`object`.trinket.Trinket
import com.github.zinc.module.item.gui.TrinketGUI
import com.github.zinc.module.item.`object`.trinket.PowerRing
import com.github.zinc.plugin
import com.github.zinc.util.addItem
import com.github.zinc.util.warn
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand

class ItemModule: Module {
    override fun registerCommands() {
        plugin.kommand {
            register("trinket", "장신구", "트링켓") {
                //open
                executes { TrinketGUI(player.uniqueId.toString()).open() }
                then("get", "name" to suggestion(Trinket.names)) {
                    requires { isOp }
                    executes {
                        val name: String by it
                        val trinket = Trinket[name] ?: run {
                            player.warn("존재하지 않는 장신구입니다.")
                            return@executes
                        }
                        player.addItem(trinket.getItem())
                    }
                }
            }
        }
    }

    override fun registerListeners() {
        with(plugin.server.pluginManager){
            registerEvents(ItemListener(), plugin)
        }
    }

    override fun register() {
        loadItems()
        super.register()
    }

    override fun onDisable() {

    }

    private fun loadItems() {
        PowerRing().register()

    }
}