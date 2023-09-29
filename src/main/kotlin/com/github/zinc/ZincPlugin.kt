package com.github.zinc;

import com.github.zinc.lib.LibraryModule
import com.github.zinc.module.item.ItemModule
import com.github.zinc.module.user.UserModule
import com.github.zinc.mongodb.MongoDB
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin;

class ZincPlugin: JavaPlugin() {

    override fun onEnable() {
        plugin = this
        MongoDB.register()
        LibraryModule().register()
        UserModule().register()
        ItemModule().register()

        //QuestManager.registerAllQuestList()
//        registerAll(
//            ServerListener(),
//            PlayerExpListener(),
//            PlayerListener(),
//            QuestListener(),
//            PlayerToolListener(),
//            PlayerWorldListener()
//        )
//        executors(
//            "status" to StatusCommand(),
//            "test" to TestCommand(),
//            "quest" to QuestCommand()
//        )
//        QuestDAO().use(QuestDAO::questTimer)
//        Recipes.registerAll()
    }

    override fun onDisable() {

    }

    private fun registerAll(vararg listener: Listener) {
        listener.forEach { plugin.server.pluginManager.registerEvents(it, this) }
    }

    private fun executors(vararg executors: Pair<String, CommandExecutor>) {
        executors.forEach { plugin.getCommand(it.first)!!.setExecutor(it.second) }
    }
}

internal const val NAMESPACE = "zinc"
internal lateinit var plugin: JavaPlugin
internal fun info(msg: Any) { plugin.logger.info(msg.toString()) }
internal fun warn(msg: Any) { plugin.logger.warning(msg.toString()) }