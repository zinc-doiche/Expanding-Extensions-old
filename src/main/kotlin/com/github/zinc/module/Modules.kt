package com.github.zinc.module

interface Module {
    fun registerCommands()

    fun registerListeners()
}

interface Configurable {
    fun read()
    fun write()
}