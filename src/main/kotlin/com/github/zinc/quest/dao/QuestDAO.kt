package com.github.zinc.quest.dao

import com.github.zinc.mybatis.QuestMapper
import com.github.zinc.quest.domain.QuestDTO
import com.github.zinc.util.AbstractDAO
import org.bukkit.entity.Enemy

class QuestDAO : AbstractDAO() {
    override val mapper: QuestMapper = sqlSession.getMapper(QuestMapper::class.java)
    fun insert(playerId: Long, questName: String) = mapper.insert(hashMapOf("appendedPlayerId" to playerId, "appendedQuestName" to questName))
    fun selectList(playerId: Long): List<QuestDTO>? = mapper.selectList(playerId)
    fun select(playerId: Long, enemy: Enemy): QuestDTO? = mapper.select(hashMapOf("playerId" to playerId, "questName" to enemy.name))
    fun update(questDTO: QuestDTO) = mapper.update(questDTO)
    fun registerQuest(questName: String, require: Int, reward: Int) {
        mapper.registerQuest(hashMapOf("questName" to questName, "questRequire" to require, "questReward" to reward))
    }
    fun resetAll() = mapper.updateAll()
    fun updateLimited(appendedQuestName: String, appendedQuestId: Long) {
        mapper.updateLimited(hashMapOf("appendedQuestName" to appendedQuestName, "appendedQuestId" to appendedQuestId))
    }
    fun selectLimitedQuestList(playerId: Long) = mapper.selectLimitedQuestList(playerId)
}