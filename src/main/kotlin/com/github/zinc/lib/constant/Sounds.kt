package com.github.zinc.lib.constant

import net.kyori.adventure.sound.Sound

object Sounds {
    val UI_OPEN = Sound.sound()
        .type(org.bukkit.Sound.UI_TOAST_IN)
        .build()

    val UI_CLOSE = Sound.sound()
        .type(org.bukkit.Sound.UI_TOAST_OUT)
        .build()

    val CHALLENGE_COMPLETED = Sound.sound()
        .type(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE)
        .build()

    val IRON_GOLEM_DAMAGED = Sound.sound()
        .type(org.bukkit.Sound.ENTITY_IRON_GOLEM_DAMAGE)
        .pitch(0F)
        .seed(2L)
        .build()

    val CLICK = Sound.sound()
        .type(org.bukkit.Sound.UI_BUTTON_CLICK)
        .build()

    val QUEST_CLEAR = Sound.sound()
        .type(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL)
        .pitch(1F)
        .seed(64L)
        .build()

     val LEVEL_UP = Sound.sound()
        .type(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP)
        .pitch(0F)
        .seed(1L)
        .build()
}
