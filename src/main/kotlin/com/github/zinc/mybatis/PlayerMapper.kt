package com.github.zinc.mybatis

import com.github.zinc.core.player.PlayerVO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface PlayerMapper: IMapper {
    fun insert(playerName: String)
    fun select(playerName: String): PlayerVO?
    fun selectById(playerId: Long): PlayerVO?
    fun update(playerVO: PlayerVO)
}