package com.github.zinc.front.event

import com.github.zinc.util.PlayerEventAdapter
import org.bukkit.entity.Enemy

class QuestClearEvent(val playerDTO: PlayerDTO, val enemy: Enemy) : PlayerEventAdapter(playerDTO.playerEntity)