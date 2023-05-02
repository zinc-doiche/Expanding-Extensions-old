package com.github.zinc;

import com.github.zinc.mybatis.MybatisConfig
import com.github.zinc.player.PlayerContainer
import com.github.zinc.player.command.StatusOpenCommand
import com.github.zinc.player.dao.PlayerDAO
import com.github.zinc.player.listener.PlayerStatusListener
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin;

class ZincPlugin: JavaPlugin() {

    override fun onEnable() {
        plugin = this
        MybatisConfig.init()
        registerAll(
            TaskManager(),
            PlayerStatusListener()
        )
        plugin.getCommand("status")?.setExecutor(StatusOpenCommand())

        TaskManager.add("updateAll") {
            if(PlayerContainer.players.isNotEmpty()) {
                info("saving...")
                val dao = PlayerDAO()
                PlayerContainer.players.values.forEach(dao::update)
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