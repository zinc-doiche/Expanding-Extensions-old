package com.github.zinc.module.user.`object`

import com.github.zinc.lib.event.UserLevelUpEvent

class Level(
    @Transient
    private val uuid: String
) {
    var level: Int = 0
        private set
    var experience: Long = 0
        private set

    fun addLevel(addedLevel: Int) {
        level += addedLevel
    }

    fun addExperience(addedExperience: Long) {
        var requiredExp = requiredExpForNextLevel(level)
        experience += addedExperience
        while(experience >= requiredExp) {
            level++
            //call event
            UserLevelUpEvent(uuid).callEvent()
            experience -= requiredExp
            requiredExp = requiredExpForNextLevel(level)
        }
    }

    private fun requiredExpForNextLevel(level: Int): Long {
        return when(level){
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
        }.toLong()
    }
}