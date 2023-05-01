package com.github.zinc.player

import com.github.zinc.player.domain.PlayerDTO
import com.github.zinc.util.domain.Container

object PlayerContainer: Container<String, PlayerDTO> {
    val players = HashMap<String, PlayerDTO>()

    override fun get(k: String): PlayerDTO? = players[k]
    override fun add(k: String, v: PlayerDTO) { players[k] = v }
    override fun remove(k: String): PlayerDTO? = players.remove(k)
}