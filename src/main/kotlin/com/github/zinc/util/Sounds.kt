package com.github.zinc.util

import net.kyori.adventure.sound.Sound

object Sounds {
    val uiOpen = Sound.sound()
        .type(org.bukkit.Sound.UI_TOAST_IN)
        .build()

    val uiClose = Sound.sound()
        .type(org.bukkit.Sound.UI_TOAST_OUT)
        .build()

    val challengeCompleted = Sound.sound()
        .type(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE)
        .build()

    val ironGolemDamaged = Sound.sound()
        .type(org.bukkit.Sound.ENTITY_IRON_GOLEM_DAMAGE)
        .pitch(0F)
        .seed(2L)
        .build()

    val click = Sound.sound()
        .type(org.bukkit.Sound.UI_BUTTON_CLICK)
        .build()

    val questClear = Sound.sound()
        .type(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL)
        .pitch(1F)
        .seed(64L)
        .build()

     val levelUp = Sound.sound()
        .type(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP)
        .pitch(0F)
        .seed(1L)
        .build()
}
