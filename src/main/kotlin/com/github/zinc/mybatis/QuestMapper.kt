package com.github.zinc.mybatis

import com.github.zinc.core.quest.QuestDTO
import com.github.zinc.core.quest.QuestVO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface QuestMapper: IMapper {
    fun insert(data: HashMap<String, Any>)
    fun selectList(playerId: Long): List<QuestDTO>?
    fun select(data: HashMap<String, Any>): QuestDTO?
    fun update(questDTO: QuestDTO)
    fun registerQuest(data: HashMap<String, Any>)
    fun updateAll()
    fun updateLimited(data: HashMap<String, Any>)
    fun selectLimitedQuestList(playerId: Long): List<Long>
    fun questTimer()
    fun selectQuest(questName: String): QuestVO
    fun getChance(playerId: Long): Int
    fun updateChance(data: HashMap<String, Any>)
    fun insertChance(playerId: Long)
}