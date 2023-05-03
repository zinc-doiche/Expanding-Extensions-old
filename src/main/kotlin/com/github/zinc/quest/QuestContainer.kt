package com.github.zinc.quest

import com.github.zinc.util.domain.Container
import org.bukkit.entity.Enemy
import org.bukkit.entity.Player

object QuestContainer: Container<Player, HashMap<Enemy, Int>>()