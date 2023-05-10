package com.github.zinc.core.quest.event

import com.github.zinc.core.player.domain.PlayerDTO
import com.github.zinc.util.event.PlayerEventAdapter
import org.bukkit.entity.Enemy

class QuestClearEvent(val playerDTO: PlayerDTO, val enemy: Enemy) : PlayerEventAdapter(playerDTO.playerEntity)