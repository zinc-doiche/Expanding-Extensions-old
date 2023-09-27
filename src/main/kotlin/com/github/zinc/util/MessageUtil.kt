package com.github.zinc.util

import org.bukkit.entity.Player

internal fun Player.warn(message: String) {
    sendMessage(com.github.zinc.util.warn(message))
}