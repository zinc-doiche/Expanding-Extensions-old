package com.github.zinc

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class TestCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        val player = if(sender is Player) sender else return list

        if(player.isOp) {
            when(args.size) {
                1 -> return StringUtil.copyPartialMatches(args[0], listOf("test"), list)
            }
        }
        return list
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }

        if (sender.isOp) {

        }

        return false
    }
}