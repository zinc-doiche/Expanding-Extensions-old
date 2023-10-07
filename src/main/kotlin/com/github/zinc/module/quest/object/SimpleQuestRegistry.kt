package com.github.zinc.module.quest.`object`

data class SimpleQuestRegistry(
    val uuid: String,
    val type: QuestType,
) {
    private val quests: MutableMap<String, SimpleQuestProcess> = HashMap()
    operator fun get(name: String) = quests[name]

    inner class SimpleQuestProcess(
        val name: String,
        val requires: Int,
    ) {
        var current: Int = 0

        val quest: SimpleQuest?
            get() = SimpleQuest[name]

        val isClear: Boolean
            get() = current >= requires
    }
}
