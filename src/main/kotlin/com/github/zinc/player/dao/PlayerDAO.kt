package com.github.zinc.player.dao

import com.github.zinc.mybatis.MybatisConfig
import com.github.zinc.mybatis.PlayerMapper
import com.github.zinc.player.domain.PlayerVO
import org.apache.ibatis.session.SqlSession
import org.bukkit.entity.Player

class PlayerDAO: AutoCloseable {
    private val sqlSession: SqlSession = MybatisConfig.sqlSessionFactory.openSession(true)
    private val playerMapper: PlayerMapper = sqlSession.getMapper(PlayerMapper::class.java)

    fun select(player: Player): PlayerVO? = playerMapper.select(player.name)
    fun insert(player: Player) = playerMapper.insert(player.name)

    override fun close() {
        sqlSession.close()
    }
}