package com.github.zinc.player.event

import com.github.zinc.util.event.PlayerEventAdapter
import org.bukkit.entity.Player

class PlayerGetExpEvent(player: Player, val amount: Int): PlayerEventAdapter(player) {

}