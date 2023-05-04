package com.github.zinc.quest

import com.github.zinc.mybatis.MybatisConfig
import com.github.zinc.mybatis.QuestMapper
import com.github.zinc.util.domain.Container
import org.bukkit.entity.Enemy
import org.bukkit.entity.Player

object QuestContainer: Container<Player, HashMap<Enemy, Int>>() {
    private val session = MybatisConfig.sqlSessionFactory.openSession()
    private val questMapper = session.getMapper(QuestMapper::class.java)
    fun getRewardOf(mob: Enemy): Int? {
        questMapper.select(mob.name)
        return 100
    }

}