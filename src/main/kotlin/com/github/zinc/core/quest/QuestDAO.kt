package com.github.zinc.core.quest

import com.github.zinc.mybatis.QuestMapper
import com.github.zinc.core.AbstractDAO
import org.bukkit.entity.Enemy

class QuestDAO : AbstractDAO() {
    override val mapper: QuestMapper = sqlSession.getMapper(QuestMapper::class.java)

    fun insert(playerId: Long, questName: String) = mapper.insert(hashMapOf(
        "appendedPlayerId" to playerId,
        "appendedQuestName" to questName
    ))
    fun selectList(playerId: Long): List<QuestDTO>? = mapper.selectList(playerId)
    fun select(playerId: Long, enemy: Enemy): QuestDTO? = mapper.select(hashMapOf(
        "playerId" to playerId,
        "questName" to enemy.name
    ))
    fun select(playerId: Long, enemyName: String): QuestDTO? = mapper.select(hashMapOf(
        "playerId" to playerId,
        "questName" to enemyName
    ))
    fun update(questDTO: QuestDTO) = mapper.update(questDTO)
    fun registerQuest(questName: String, require: Int, reward: Int, questType: String) {
        mapper.registerQuest(hashMapOf(
            "questName" to questName,
            "questRequire" to require,
            "questReward" to reward,
            "questType" to questType
        ))
    }
    fun resetAll() = mapper.updateAll()
    fun updateLimited(appendedQuestName: String, appendedQuestId: Long) {
        mapper.updateLimited(hashMapOf(
            "appendedQuestName" to appendedQuestName,
            "appendedQuestId" to appendedQuestId
        ))
    }
    fun selectLimitedQuestList(playerId: Long) = mapper.selectLimitedQuestList(playerId)
    fun questTimer() = mapper.questTimer()
    fun selectQuest(questName: String) = mapper.selectQuest(questName)
    fun getChance(playerId: Long) = mapper.getChance(playerId)
    fun updateChance(playerId: Long, questResetChance: Int) = mapper.updateChance(hashMapOf(
        "playerId" to playerId,
        "questResetChance" to questResetChance
    ))

    fun insertChance(playerId: Long) = mapper.insertChance(playerId)
}