package com.github.zinc.lib

import com.github.zinc.lib.gui.GUIListener
import com.github.zinc.module.Module
import com.github.zinc.module.item.`object`.trinket.Trinket
import com.github.zinc.module.user.`object`.User
import com.github.zinc.plugin
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import org.bukkit.attribute.Attribute

object LibraryModule: Module {
    override fun registerCommands() {
        plugin.kommand {
            register("test") {
                then("user") {
                    requires { isOp }
                    executes {
                        player.sendMessage("user: ${User[player]!!}")
                    }
                }
                then("me") {
                    requires { isOp }
                    executes {
                        player.sendMessage("user: ${User[player]!!}")
                    }
                }
                then("trinket") {
                    requires { isOp }
                    executes {
                        player.sendMessage(Trinket.names.toString())
                    }
                }
                then("attribute", "type" to suggestion(Attribute.entries)) {
                    requires { isOp }
                    executes {
                        val type: String by it
                        player.getAttribute(Attribute.valueOf(type))?.let { attribute ->
                            var message = text("$type: ")
                                .appendNewline()
                                .append(text("value: ${attribute.value}"))
                                .appendNewline()
                                .append(text("baseValue: ${attribute.baseValue}"))
                                .appendNewline()
                            attribute.modifiers.forEach { modifier ->
                                message = message
                                    .append(text("${modifier.name}: ${modifier.operation.name}(${modifier.amount}), uuid: ${modifier.uniqueId}"))
                                    .appendNewline()
                            }
                            player.sendMessage(message)
                        }
                    }
                }
                then("removeAllModifiers",  "type" to suggestion(Attribute.entries)) {
                    requires { isOp }
                    executes {
                        val type: String by it
                        player.getAttribute(Attribute.valueOf(type))?.let { attribute ->
                            attribute.modifiers.forEach { modifier ->
                                attribute.removeModifier(modifier)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun registerListeners() {
        plugin.server.pluginManager.registerEvents(GUIListener(), plugin)
    }

    override fun onDisable() {

    }
}