package com.github.zinc.front.event

import com.github.zinc.core.player.PlayerData
import com.github.zinc.util.PlayerEventAdapter
import org.bukkit.entity.Enemy

class QuestClearEvent(val playerData: PlayerData, val enemy: Enemy) : PlayerEventAdapter(playerData.manager!!.playerEntity)