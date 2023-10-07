package com.github.zinc.module.quest

import com.github.zinc.module.Module
import com.github.zinc.module.quest.gui.QuestGUI
import com.github.zinc.module.quest.listener.SimpleQuestListener
import com.github.zinc.module.quest.`object`.Quest
import com.github.zinc.module.quest.`object`.QuestType
import com.github.zinc.module.quest.`object`.SimpleQuest
import com.github.zinc.module.quest.`object`.SimpleQuestRegistry
import com.github.zinc.module.user.UserModule
import com.github.zinc.module.user.`object`.User
import com.github.zinc.mongodb.Document
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.mongodb.set
import com.github.zinc.plugin
import com.github.zinc.util.async
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.`in`
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.kommand.kommand
import kotlinx.coroutines.async
import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.timer

object QuestModule: Module {
    private const val QUEST_FILE = "quest.yml"
    private const val DAILY_QUEST_COUNT = 6
    private const val SPECIAL_QUEST_COUNT = 3
    private const val WEEK_QUEST_COUNT = 3

    lateinit var initDate: LocalDateTime
    lateinit var initWeekDate: LocalDateTime

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
        registerQuests()

        val today = LocalDate.now()
        initDate = LocalDateTime.of(today, LocalTime.of(24, 0, 0))
        initWeekDate = LocalDateTime.of(today.plusDays(7 - today.dayOfWeek.value.toLong()), LocalTime.of(24, 0, 0))

        timer(startAt = Timestamp.valueOf(initDate), period = 1000 * 60 * 60 * 24) {
            HeartbeatScope().async {
                plugin.server.broadcast(Component.text("일일 임무 초기화 중..."))
                initQuests(QuestType.DAILY, DAILY_QUEST_COUNT)
                plugin.server.broadcast(Component.text("일일 임무가 초기화되었습니다."))
            }
        }
        timer(startAt = Timestamp.valueOf(initWeekDate), period = 1000 * 60 * 60 * 24 * 7) {
            HeartbeatScope().async {
                plugin.server.broadcast(Component.text("주간 임무 초기화 중..."))
                initQuests(QuestType.WEEKLY, WEEK_QUEST_COUNT)
                plugin.server.broadcast(Component.text("주간 임무가 초기화되었습니다."))
            }
        }

        super.register()
    }

    override fun onDisable() {

    }

    private fun initQuests(type: QuestType, count: Int) {
        SimpleQuest.list.filter { it.type == type }.let { quests ->
            val newMap: MutableMap<String, SimpleQuest> = HashMap()
            while(newMap.size < count) {
                val quest = quests.random()
                newMap[quest.name] = quest
            }
            SimpleQuest.Container[type] = newMap
        }
    }

    private fun registerQuests() {
        val questFile = File(plugin.dataFolder, QUEST_FILE)

        if(!questFile.exists()) {
            plugin.saveResource(QUEST_FILE, false)
        }

        val register = register@{ name: String, section: Any ->
            section as ConfigurationSection
            val questType = QuestType.valueOf((section.getString("") ?: return@register).uppercase(Locale.getDefault()))
            val requires = section.getInt("requires")
            val rewards = section.getInt("rewards")
            SimpleQuest[name] = SimpleQuest(name, questType, requires, rewards)
        }

        YamlConfiguration.loadConfiguration(questFile).let { yml ->
            val dailyQuests = yml.getConfigurationSection("daily") ?: return
            val weekQuests = yml.getConfigurationSection("weekly") ?: return

            dailyQuests.getValues(true).forEach(register)
            weekQuests.getValues(true).forEach(register)
        }
    }

    fun updateQuests(type: QuestType) {
        MongoDB["quest"].run {
            MongoDB.transaction {
                deleteMany(eq("type", type.name))

                MongoDB["user"]
                    .find()
                    .map { user ->
                        Document {
                            put("uuid", user["uuid"])
                            put("type", type.name)
                            put("quests", SimpleQuest.Container[type])
                        }
                    }
            }
        }
    }
}