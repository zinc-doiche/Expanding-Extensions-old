package com.github.zinc.util

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

abstract class PlayerEventAdapter(player: Player): PlayerEvent(player) {
    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }
    companion object {
        @JvmStatic
        fun getHandlerList() = handlers
        private val handlers: HandlerList = HandlerList()
    }
}