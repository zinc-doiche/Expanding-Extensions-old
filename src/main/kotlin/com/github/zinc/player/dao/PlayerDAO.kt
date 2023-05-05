package com.github.zinc.player.dao

import com.github.zinc.mybatis.PlayerMapper
import com.github.zinc.player.domain.PlayerDTO
import com.github.zinc.player.domain.PlayerVO
import com.github.zinc.util.AbstractDAO
import org.bukkit.entity.Player

class PlayerDAO : AbstractDAO() {
    override val mapper: PlayerMapper = sqlSession.getMapper(PlayerMapper::class.java)

    fun select(player: Player): PlayerVO? = mapper.select(player.name)
    fun insert(player: Player) = mapper.insert(player.name)
    fun update(playerDTO: PlayerDTO) = mapper.update(playerDTO)
}