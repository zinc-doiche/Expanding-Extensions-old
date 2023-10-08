package com.github.zinc.module.quest.`object`

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoIterable

data class SimpleQuestRegistry(
    val uuid: String,
    val type: QuestType,
) {
    private var quests: MutableMap<String, SimpleQuestProcess> = HashMap()
    operator fun get(name: String) = quests[name]

    fun update(updateQuests: Collection<SimpleQuest>) {
        quests = HashMap()
        updateQuests.forEach {
            quests[it.name] = SimpleQuestProcess(it.name, it.requires)
        }
    }

    fun update(updateQuests: MongoIterable<SimpleQuest>) {
        quests = HashMap()
        updateQuests.forEach {
            quests[it.name] = SimpleQuestProcess(it.name, it.requires)
        }
    }

    inner class SimpleQuestProcess(
        val name: String,
        val requires: Int,
        var current: Int = 0
    ) {

        val quest: SimpleQuest?
            get() = SimpleQuest[name]

        val isClear: Boolean
            get() = current >= requires
    }
}
