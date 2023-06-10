package com.github.zinc.front.event

import com.github.zinc.core.player.PlayerData
import org.bukkit.entity.Enemy

class QuestClearEvent(val playerData: PlayerData, val enemy: Enemy) : EventAdapter(true)