package com.github.zinc;

import com.github.zinc.mybatis.MybatisConfig
import com.github.zinc.player.domain.PlayerContainer
import com.github.zinc.player.command.StatusOpenCommand
import com.github.zinc.player.dao.PlayerDAO
import com.github.zinc.player.listener.PlayerExpListener
import com.github.zinc.player.listener.PlayerStatusListener
import com.github.zinc.quest.dao.QuestDAO
import com.github.zinc.quest.listener.QuestListener
import com.github.zinc.quest.manager.QuestManager
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin;

class ZincPlugin: JavaPlugin() {

    override fun onEnable() {
        plugin = this
        MybatisConfig.init()

        //QuestManager.registerAllQuestList()

        registerAll(
            TaskManager(),
            PlayerStatusListener(),
            PlayerExpListener(),
            QuestListener()
        )
        plugin.getCommand("status")?.setExecutor(StatusOpenCommand())
        TaskManager.add("updateAll") {
            if(PlayerContainer.container.isNotEmpty()) {
                info("saving...")
                PlayerDAO().use { PlayerContainer.container.values.forEach(it::update) }
            }
        }
    }

    override fun onDisable() {

    }

    private fun registerAll(vararg listener: Listener) {
        listener.forEach { plugin.server.pluginManager.registerEvents(it, this) }
    }
}

internal lateinit var plugin: JavaPlugin
internal fun info(msg: Any) { plugin.logger.info(msg.toString()) }
internal fun warn(msg: Any) { plugin.logger.warning(msg.toString()) }