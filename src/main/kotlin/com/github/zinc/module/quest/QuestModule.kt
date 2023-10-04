package com.github.zinc.module.quest

import com.github.zinc.module.Module
import com.github.zinc.module.quest.gui.QuestGUI
import com.github.zinc.module.quest.listener.SimpleQuestListener
import com.github.zinc.plugin
import io.github.monun.kommand.kommand
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.concurrent.timer

class QuestModule: Module {
    override fun registerCommands() {
        plugin.kommand {
            register("quest") {
                executes { QuestGUI().open() }
            }
        }
    }

    override fun registerListeners() {
        with(plugin.server.pluginManager) {
            registerEvents(SimpleQuestListener(), plugin)
        }
    }

    override fun register() {
        val initDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(24, 0, 0))
        timer(startAt = Timestamp.valueOf(initDate), period = 1000 * 60 * 60 * 24) {
            initQuests()
        }
        super.register()
    }

    override fun onDisable() {

    }

    private fun initQuests() {

    }
}