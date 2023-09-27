package com.github.zinc.module

import org.bukkit.event.Listener

interface Module {
    fun registerCommands()
    fun registerListeners()

    fun register() {
        registerCommands()
        registerListeners()
    }
}

interface Configurable {
    fun read()
    fun write()
}