package com.github.zinc.mybatis

import org.apache.ibatis.annotations.Mapper

@Mapper
interface QuestMapper {
    /**
     * name -> ( require , reward )
     */
    fun select(questMobName: String): Map<String, Any>?
}