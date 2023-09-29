package com.github.zinc.module

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