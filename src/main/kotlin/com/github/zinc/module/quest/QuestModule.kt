package com.github.zinc.module.quest

import com.github.zinc.info
import com.github.zinc.module.Module
import com.github.zinc.module.quest.gui.QuestGUI
import com.github.zinc.module.quest.listener.SimpleQuestListener
import com.github.zinc.module.quest.`object`.QuestType
import com.github.zinc.module.quest.`object`.SimpleQuest
import com.github.zinc.module.user.`object`.User
import com.github.zinc.module.user.util.toDocument
import com.github.zinc.mongodb.documentOf
import com.github.zinc.mongodb.MongoDB
import com.github.zinc.mongodb.set
import com.github.zinc.mongodb.toObject
import com.github.zinc.plugin
import com.mongodb.client.model.Filters.*
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.kommand.kommand
import kotlinx.coroutines.async
import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.concurrent.timer

object QuestModule: Module {
    private const val QUEST_FILE = "quest.yml"
    private const val DAILY_QUEST_COUNT = 7
    private const val WEEK_QUEST_COUNT = 3

    lateinit var initDate: LocalDateTime
        private set
    lateinit var initWeekDate: LocalDateTime
        private set

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
        val scope = HeartbeatScope()
        val today = LocalDate.now()
        initDate = LocalDateTime.of(today, LocalTime.of(24, 0, 0))
        initWeekDate = LocalDateTime.of(today.plusDays(7 - today.dayOfWeek.value.toLong()), LocalTime.of(24, 0, 0))

        info("퀘스트를 불러오는 중...")
        registerQuests()
        info("퀘스트를 모두 불러왔습니다!")

        timer(startAt = Timestamp.valueOf(initDate), period = 1000 * 60 * 60 * 24) {
            scope.async {
                plugin.server.broadcast(Component.text("일일 임무 초기화 중..."))
                updateQuests(QuestType.DAILY, DAILY_QUEST_COUNT)
                plugin.server.broadcast(Component.text("일일 임무가 초기화되었습니다."))
            }
        }
        timer(startAt = Timestamp.valueOf(initWeekDate), period = 1000 * 60 * 60 * 24 * 7) {
            scope.async {
                plugin.server.broadcast(Component.text("주간 임무 초기화 중..."))
                updateQuests(QuestType.WEEKLY, WEEK_QUEST_COUNT)
                plugin.server.broadcast(Component.text("주간 임무가 초기화되었습니다."))
            }
        }

        super.register()
    }

    override fun onDisable() {}

    private fun registerQuests() {
        val questFile = File(plugin.dataFolder, QUEST_FILE)
        val collection = MongoDB["quest"]
        if(!questFile.exists()) {
            plugin.saveResource(QUEST_FILE, false)
        }
        val register = register@{ name: String, section: Any ->
            section as ConfigurationSection
            collection
                .find(eq("name", name))
                .firstOrNull()
                ?: run {
                    val questType = QuestType.valueOf((section.getString("") ?: return@register).uppercase(Locale.getDefault()))
                    val requires = section.getInt("requires")
                    val rewards = section.getInt("rewards")
                    collection.insertOne(documentOf(
                        "name" to name,
                        "questType" to questType,
                        "requires" to requires,
                        "rewards" to rewards,
                        "activated" to false
                    ))
                }
        }

        YamlConfiguration.loadConfiguration(questFile).let { yml ->
            val dailyQuests = yml.getConfigurationSection("daily") ?: return
            val weekQuests = yml.getConfigurationSection("weekly") ?: return
            dailyQuests.getValues(true).forEach(register)
            weekQuests.getValues(true).forEach(register)
        }
    }

    private fun updateQuests(type: QuestType, size: Int) {
        MongoDB.transaction {
            val newSet = HashSet<SimpleQuest>()
            val questCollection = MongoDB["quest"]
            val userCollection = MongoDB["user"]
            val questList = questCollection
                .find(eq("activated", false))
                .toList()
            while (newSet.size < size) {
                val quest = questList.random().toObject(SimpleQuest::class)
                newSet.add(quest)
            }
            questCollection.updateMany(and(eq("activated", true), eq("type", type)), set("activated", false))
            questCollection.updateMany(`in`("name", newSet), set("activated", true))
            User.getUsers().forEach { user ->
                user.questRegistries[type]?.update(newSet)
                userCollection.replaceOne(eq("uuid", user.uuid), user.toDocument())
            }
        }
    }
}