package com.github.zinc.module.user.`object`

import com.github.zinc.lib.event.AsyncUserLevelUpEvent
import com.github.zinc.util.async
import org.bukkit.Bukkit

internal const val MAX_LEVEL = 300

class Level {
    var level: Int = 0
        private set
    var experience: Long = 0
        private set

    val isMax: Boolean
        get() = level >= MAX_LEVEL

    fun addLevel(addedLevel: Int) {
        level += addedLevel
    }

    fun addExperience(addedExperience: Long, user: User) {
        var requiredExp = requiredExpForNextLevel
        experience += addedExperience
        while(experience >= requiredExp) {
            level++
            //call event
            experience -= requiredExp
            requiredExp = requiredExpForNextLevel
            async { Bukkit.getPluginManager().callEvent(AsyncUserLevelUpEvent(user)) }
        }
    }

    val requiredExpForNextLevel: Int
        get() = when(level) {
            in 0..99 ->
                (-1 / 500.0) * level * level * (level - 150) + 100
            //1100
            in 100..199 ->
                (-1 / 50.0) * (level - 100) * (level - 100) * (level - 250) + 1100
            //11100
            in 200..299 ->
                (-1 / 5.0) * (level - 100) * (level - 100) * (level - 250) + 11100
            //111100
            else -> -1
        }.toInt()

    override fun toString(): String {
        return "Level(level=$level, experience=$experience)"
    }
}