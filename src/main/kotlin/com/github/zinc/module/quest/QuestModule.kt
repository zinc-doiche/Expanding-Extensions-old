package com.github.zinc.module.quest

import com.github.zinc.module.Module
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.concurrent.timer

class QuestModule: Module {
    override fun registerCommands() {

    }

    override fun registerListeners() {

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