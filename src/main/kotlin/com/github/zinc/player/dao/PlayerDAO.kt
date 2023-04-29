package com.github.zinc.player.dao

import com.github.zinc.mybatis.MybatisConfig
import com.github.zinc.mybatis.mapper.classMapper.PlayerMapper
import com.github.zinc.player.domain.PlayerDTO
import com.github.zinc.player.domain.PlayerVO
import org.apache.ibatis.session.SqlSession
import org.bukkit.entity.Player

class PlayerDAO: AutoCloseable {
    private val sqlSession: SqlSession = MybatisConfig.sqlSessionFactory.openSession()
    private val playerMapper: PlayerMapper = sqlSession.getMapper(PlayerMapper::class.java)

    fun select(player: Player): PlayerVO? = sqlSession.selectOne("com.github.zinc.mybatis.mapper.classMapper.PlayerMapper.select", player.name)
    fun insert(player: Player) = playerMapper.insert(player.name)

    override fun close() {
        sqlSession.close()
    }
}