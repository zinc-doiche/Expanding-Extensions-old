package com.github.zinc.front.event

import com.github.zinc.util.PlayerEventAdapter
import org.bukkit.entity.Player

class PlayerGetExpEvent(player: Player, val amount: Int): PlayerEventAdapter(player)