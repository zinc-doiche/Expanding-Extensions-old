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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuestDTO

        if (appendedQuestId != other.appendedQuestId) return false

        return true
    }

    override fun hashCode(): Int {
        return appendedQuestId.hashCode()
    }

    override fun toString(): String {
        return "QuestDTO(appendedQuestId=$appendedQuestId, appendedPlayerId=$appendedPlayerId, appendedQuestName='$appendedQuestName', appendedQuestProgress=$appendedQuestProgress, questType=$questType, questRequire=$questRequire, questReward=$questReward, appendedQuestCleared=$appendedQuestCleared)"
    }
}