package com.github.zinc.player.event

import com.github.zinc.player.domain.StatusType
import com.github.zinc.util.event.PlayerEventAdapter
import org.bukkit.entity.Player

class PlayerStatusChangeEvent(player: Player, val changes: HashMap<StatusType, Int>): PlayerEventAdapter(player)