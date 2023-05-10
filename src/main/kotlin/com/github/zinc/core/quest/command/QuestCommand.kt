package com.github.zinc.core.quest.command

import com.github.zinc.core.player.domain.PlayerContainer
import com.github.zinc.core.quest.fx.QuestFx
import io.github.monun.invfx.openFrame
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class QuestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }

        sender.openFrame(QuestFx.getQuestMainFx(PlayerContainer[sender.name]!!))

        return true
    }
}