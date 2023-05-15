package com.github.zinc.command

import com.github.zinc.container.PlayerContainer
import com.github.zinc.core.player.StatusType
import com.github.zinc.front.ui.StatusFx
import com.github.zinc.util.Sounds
import io.github.monun.invfx.openFrame
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class StatusOpenCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        val player = if(sender is Player) sender else return list

        if(player.isOp) {
            when(args.size) {
                1 -> return StringUtil.copyPartialMatches(args[0], listOf("open", "view", "add"), list)
                2 -> if(args[0] == "add")
                    return StringUtil.copyPartialMatches(args[1], listOf("str", "swt", "bal", "con", "rem", "lv"), list)
            }
        } else {
            when(args.size) {
                1 -> return StringUtil.copyPartialMatches(args[0], listOf("open", "view"), list)
            }
        }
        return list
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }
        val playerData = PlayerContainer[sender.name]!!

        when (args.size) {
            1 -> {
                when(args[0]) {
                    "open" -> {
                        sender.playSound(Sounds.uiOpen)
                        sender.openFrame(StatusFx.getStatusFrame(playerData))
                        return true
                    }
                    "view" -> {
                        sender.playSound(Sounds.uiOpen)
                        sender.sendMessage(
                            "${sender.name}의 스테이터스 :\n" +
                            "[${playerData.playerVO.playerLevel}Lv.] ${playerData.playerVO.playerExperience}xp / ${playerData.manager?.getMaxExpForNextLevel()}xp\n" +
                            "| Strength: ${playerData.playerVO.playerStrength}\n" +
                            "| Swiftness: ${playerData.playerVO.playerSwiftness}\n" +
                            "| Balance: ${playerData.playerVO.playerBalance}\n" +
                            "| Concentration: ${playerData.playerVO.playerConcentration}\n" +
                            "\n" +
                            "잔여스탯: ${playerData.playerVO.playerStatusRemain}"
                        )
                        return true
                    }
                }
            }
        }
        if(sender.isOp) when (args.size) {
            //ex) add str 3
            3 -> {
                val amount = args[2].toIntOrNull() ?: return false
                when(args[1]) {
                    "str" -> {
                        playerData.manager?.updateStatus(StatusType.STRENGTH, amount)
                        playerData.manager?.applyStatus(StatusType.STRENGTH)
                        return true
                    }
                    "swt" -> {
                        playerData.manager?.updateStatus(StatusType.SWIFTNESS, amount)
                        playerData.manager?.applyStatus(StatusType.SWIFTNESS)
                        return true
                    }
                    "bal" -> {
                        playerData.manager?.updateStatus(StatusType.BALANCE, amount)
                        playerData.manager?.applyStatus(StatusType.BALANCE)
                        return true
                    }
                    "con" -> {
                        playerData.manager?.updateStatus(StatusType.CONCENTRATION, amount)
                        playerData.manager?.applyStatus(StatusType.CONCENTRATION)
                        return true
                    }
                    "rem" -> {
                        playerData.manager?.updateStatus(StatusType.REMAIN, amount)
                        return true
                    }
                    "lv" -> {
                        playerData.playerVO.playerExperience = 0
                        playerData.manager?.levelUp(amount)
                        return true
                    }
                }
            }
        }
        return false
    }
}