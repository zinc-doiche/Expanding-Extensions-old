package com.github.zinc.core.player.dao

import com.github.zinc.mybatis.PlayerMapper
import com.github.zinc.core.player.PlayerVO
import com.github.zinc.util.AbstractDAO
import org.bukkit.entity.Player

class PlayerDAO : AbstractDAO() {
    override val mapper: PlayerMapper = sqlSession.getMapper(PlayerMapper::class.java)

    fun select(player: Player): PlayerVO? = mapper.select(player.name)
    fun select(playerName: String): PlayerVO? = mapper.select(playerName)
    fun select(playerId: Long): PlayerVO? = mapper.selectById(playerId)
    fun insert(player: Player) = mapper.insert(player.name)
    fun insert(playerName: String) = mapper.insert(playerName)
    fun update(playerVO: PlayerVO) = mapper.update(playerVO)
}