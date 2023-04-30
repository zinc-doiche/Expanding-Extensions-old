package com.github.zinc.player.command

import com.github.zinc.player.PlayerContainer
import com.github.zinc.player.fx.StatusFx
import com.github.zinc.util.component.text
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.openFrame
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class StatusOpenCommmand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        when(args.size) {
            1 -> return StringUtil.copyPartialMatches(args[0], listOf("open", "view"), list)
        }
        return list
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }
        when (args.size) {
            1 -> {
                when(args[0]) {
                    "open" -> sender.openFrame(InvFX.frame(4, text("ji")){})
                    "view" -> {
                        val playerDTO = PlayerContainer[sender.name]!!
                        sender.sendMessage(
                            "${sender.name}의 스테이터스 :\n" +
                            "| Strength: ${playerDTO.playerStrength}\n" +
                            "| Swiftness: ${playerDTO.playerSwiftness}\n" +
                            "| Balance: ${playerDTO.playerBalance}\n" +
                            "| Concentration: ${playerDTO.playerConcentration}\n"
                        )
                    }
                }
            }
        }
        return false
    }
}