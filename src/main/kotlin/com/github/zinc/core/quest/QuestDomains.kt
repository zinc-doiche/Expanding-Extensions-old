package com.github.zinc.core.quest

data class QuestVO(
    var questName: String,
    var questRequire: Int,
    var questReward: Int,
    var questType: String
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
    fun apply(questVO: QuestVO): QuestDTO = run {
        appendedQuestName = questVO.questName
        questRequire = questVO.questRequire
        questReward = questVO.questReward
        questType = questVO.questType
        this
    }
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