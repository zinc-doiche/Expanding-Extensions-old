package com.github.zinc.module.quest.`object`

data class SimpleQuestProcess(
    val name: String,
    val type: QuestType,
    var current: Int = 0
) {
    val quest: SimpleQuest?
        get() = SimpleQuest[name]
}