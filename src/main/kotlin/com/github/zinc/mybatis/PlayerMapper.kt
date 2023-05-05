package com.github.zinc.mybatis

import com.github.zinc.player.domain.PlayerDTO
import com.github.zinc.player.domain.PlayerVO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface PlayerMapper: IMapper {
    fun insert(playerName: String)
    fun select(playerName: String): PlayerVO?
    fun update(playerDTO: PlayerDTO)
}