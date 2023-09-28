package com.github.zinc.module.item.`object`

import org.bukkit.entity.Player

interface Passive {
    fun on(player: Player)
    fun off(player: Player)
}

interface Active {
    fun active()

    companion object {
        private val onCloseHit: Set<() -> Unit> = HashSet()
        private val onLongHit: Set<() -> Unit> = HashSet()
        private val onHitDetection: Set<() -> Unit> = HashSet()
    }
}