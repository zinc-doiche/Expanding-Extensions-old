package com.github.zinc.quest.domain

data class QuestVO(
    var appendedQuestId: Long = 0L,
    var appendedPlayerId: Long,
    var appendedQuestName: String,
    var appendedQuestProgress: Int = 0,
    var appendedQuestCleared: Boolean = false
)

data class QuestDTO(
    var appendedQuestId: Long,
    var appendedPlayerId: Long,
    var appendedQuestName: String,
    var appendedQuestProgress: Int,
    var questType: String,
    var questRequire: Int,
    var questReward: Int,
    var appendedQuestCleared: Boolean
)