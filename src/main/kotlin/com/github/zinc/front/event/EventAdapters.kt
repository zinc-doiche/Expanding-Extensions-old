package com.github.zinc.front.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

abstract class PlayerEventAdapter(player: Player, async: Boolean = false): PlayerEvent(player, async) {
    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }
    companion object {
        @JvmStatic
        fun getHandlerList() = handlers
        private val handlers: HandlerList = HandlerList()
    }
}

abstract class EventAdapter(async: Boolean = false): Event(async) {
    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }
    companion object {
        @JvmStatic
        fun getHandlerList() = handlers
        private val handlers: HandlerList = HandlerList()
    }
}