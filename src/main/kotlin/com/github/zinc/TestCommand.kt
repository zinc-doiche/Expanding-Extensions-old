package com.github.zinc

import com.github.zinc.util.extension.text
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.openFrame
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }

        if (sender.isOp) {
            InvFX.frame(6, text("test")) {
                list(1, 1, 3, 3, true, { listOf("1", "2", "3", "4", "5", "6", "1", "2", "3", "4", "5", "6") }) {
                    transform { com.github.zinc.util.extension.item(Material.DIAMOND, text(it)) }
                }
            }.let(sender::openFrame)
        }

        return false
    }
}