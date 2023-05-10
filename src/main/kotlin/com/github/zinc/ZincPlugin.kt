package com.github.zinc;

import com.github.zinc.mybatis.MybatisConfig
import com.github.zinc.command.StatusOpenCommand
import com.github.zinc.core.player.dao.PlayerDAO
import com.github.zinc.core.player.listener.PlayerExpListener
import com.github.zinc.core.player.listener.PlayerListener
import com.github.zinc.core.player.listener.PlayerStatusListener
import com.github.zinc.command.QuestCommand
import com.github.zinc.core.quest.dao.QuestDAO
import com.github.zinc.core.quest.listener.QuestListener
import com.github.zinc.front.listener.ServerListener
import com.github.zinc.core.TestCommand
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin;

class ZincPlugin: JavaPlugin() {

    override fun onEnable() {
        plugin = this
        MybatisConfig.init()

        //QuestManager.registerAllQuestList()

        registerAll(
            ServerListener(),
            PlayerStatusListener(),
            PlayerExpListener(),
            PlayerListener(),
            QuestListener()
        )
        executors(
            "status" to StatusOpenCommand(),
            "test" to TestCommand(),
            "quest" to QuestCommand()
        )
        ServerListener.add("updateAll") {
            if(PlayerContainer.container.isNotEmpty()) {
                info("saving...")
                PlayerDAO().use { PlayerContainer.container.values.forEach(it::update) }
            }
        }

        QuestDAO().use(QuestDAO::questTimer)
    }

    override fun onDisable() {
        if(PlayerContainer.container.isNotEmpty()) {
            info("saving...")
            PlayerDAO().use { PlayerContainer.container.values.forEach(it::update) }
        }
    }

    private fun registerAll(vararg listener: Listener) {
        listener.forEach { plugin.server.pluginManager.registerEvents(it, this) }
    }

    private fun executors(vararg executors: Pair<String, CommandExecutor>) {
        executors.forEach { plugin.getCommand(it.first)!!.setExecutor(it.second) }
    }
}

internal lateinit var plugin: JavaPlugin
internal fun info(msg: Any) { plugin.logger.info(msg.toString()) }
internal fun warn(msg: Any) { plugin.logger.warning(msg.toString()) }