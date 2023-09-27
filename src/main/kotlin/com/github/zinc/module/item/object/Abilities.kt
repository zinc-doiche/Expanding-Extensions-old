package com.github.zinc.module.item.`object`

import org.bukkit.entity.Player

interface Passive {
    fun on(player: Player)
    fun off(player: Player)
}

interface Active {
    fun active()
}