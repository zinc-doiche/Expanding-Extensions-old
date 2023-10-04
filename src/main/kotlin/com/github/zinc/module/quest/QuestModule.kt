package com.github.zinc.module.quest

import com.github.zinc.module.Module
import com.github.zinc.module.quest.gui.QuestGUI
import com.github.zinc.module.quest.listener.SimpleQuestListener
import com.github.zinc.module.quest.`object`.Quest
import com.github.zinc.module.quest.`object`.QuestType
import com.github.zinc.module.quest.`object`.SimpleQuest
import com.github.zinc.plugin
import io.github.monun.kommand.kommand
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.concurrent.timer

class QuestModule: Module {
    override fun registerCommands() {
        plugin.kommand {
            register("quest") {
                executes { QuestGUI(player.uniqueId.toString()).open() }
            }
        }
    }

    override fun registerListeners() {
        with(plugin.server.pluginManager) {
            registerEvents(SimpleQuestListener(), plugin)
        }
    }

    override fun register() {
        val today = LocalDate.now()

        val initDate = LocalDateTime.of(today, LocalTime.of(24, 0, 0))
        val initWeekDate = LocalDateTime.of(today.plusDays(7 - today.dayOfWeek.value.toLong()), LocalTime.of(24, 0, 0))

        timer(startAt = Timestamp.valueOf(initDate), period = 1000 * 60 * 60 * 24) {
            initQuests(QuestType.DAILY)
        }
        timer(startAt = Timestamp.valueOf(initWeekDate), period = 1000 * 60 * 60 * 24 * 7) {
            initQuests(QuestType.WEEKLY)
        }

        super.register()
    }

    override fun onDisable() {

    }

    private fun initQuests(type: QuestType) {
        val quests = Quest.list.filter {
            it as SimpleQuest
            it.type == type
        }
    }

    companion object {
        private const val QUEST_FILE = "quest.yml"

        fun registerQuests() {
            val questFile = File(plugin.dataFolder, QUEST_FILE)

            if(!questFile.exists()) {
                plugin.saveResource(QUEST_FILE, false)
            }

            val register = register@{ name: String, section: Any ->
                section as ConfigurationSection
                val questType = QuestType.valueOf((section.getString("") ?: return@register).uppercase(Locale.getDefault()))
                val requires = section.getInt("requires")
                val rewards = section.getInt("rewards")
                Quest[name] = SimpleQuest(name, questType, requires, rewards)
            }

            YamlConfiguration.loadConfiguration(questFile).let { yml ->
                val dailyQuests = yml.getConfigurationSection("daily") ?: return
                val specialQuests = yml.getConfigurationSection("special") ?: return
                val weekQuests = yml.getConfigurationSection("weekly") ?: return

                dailyQuests.getValues(true).forEach(register)
                specialQuests.getValues(true).forEach(register)
                weekQuests.getValues(true).forEach(register)
            }
        }
    }
}