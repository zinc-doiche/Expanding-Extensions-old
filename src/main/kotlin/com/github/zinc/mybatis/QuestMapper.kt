package com.github.zinc.mybatis

import com.github.zinc.quest.domain.QuestDTO
import com.github.zinc.quest.domain.QuestVO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface QuestMapper: IMapper {
    fun insert(data: HashMap<String, Any>)
    fun selectList(playerId: Long): List<QuestDTO>?
    fun select(data: HashMap<String, Any>): QuestDTO?
    fun update(questDTO: QuestDTO)
    fun registerQuest(data: HashMap<String, Any>)
}