package com.github.zinc;

import com.github.zinc.lib.LibraryModule
import com.github.zinc.module.Module
import com.github.zinc.module.item.ItemModule
import com.github.zinc.module.quest.QuestModule
import com.github.zinc.module.recipe.RecipeModule
import com.github.zinc.module.user.UserModule
import com.github.zinc.module.world.WorldModule
import com.github.zinc.mongodb.MongoDB
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin;

class ExpandingExtensionPlugin: JavaPlugin() {
    private val modules = ArrayList<Module>()

    override fun onEnable() {
        plugin = this
        MongoDB.register()

        modules.add(LibraryModule())
        modules.add(ItemModule())
        modules.add(RecipeModule())

        modules.add(UserModule())
        modules.add(QuestModule())
        modules.add(WorldModule())

        modules.forEach(Module::register)
    }

    override fun onDisable() {
        modules.forEach(Module::onDisable)
    }
}

internal lateinit var plugin: JavaPlugin
internal fun info(msg: Any) { plugin.logger.info(msg.toString()) }
internal fun warn(msg: Any) { plugin.logger.warning(msg.toString()) }