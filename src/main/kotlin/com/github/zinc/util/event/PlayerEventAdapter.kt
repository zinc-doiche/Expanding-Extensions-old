package com.github.zinc.util.event

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

open class PlayerEventAdapter(player: Player): PlayerEvent(player) {
    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }
    companion object {
        @JvmStatic
        fun getHandlerList() = handlers
        private val handlers: HandlerList = HandlerList()
    }
}