package com.github.zinc.module

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandArgumentSupport
import kotlin.enums.EnumEntries

interface Module {
    fun registerCommands()
    fun registerListeners()
    fun onDisable()

    fun register() {
        registerCommands()
        registerListeners()
    }

    fun KommandArgumentSupport.suggestion(iterable: Iterable<String>): KommandArgument<String> {
        val argument = string()
        argument.suggests {
            suggest(iterable)
        }
        return argument
    }

    fun <E : Enum<E>> KommandArgumentSupport.suggestion(enumEntries: EnumEntries<E>): KommandArgument<String> {
        val argument = string()
        argument.suggests {
            suggest(enumEntries.map { it.name })
        }
        return argument
    }
}

interface Configurable {
    fun read()
    fun write()
}