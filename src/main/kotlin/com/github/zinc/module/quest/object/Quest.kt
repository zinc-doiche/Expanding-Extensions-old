package com.github.zinc.module.quest.`object`

import com.github.zinc.module.user.`object`.User
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

interface Quest {
    val name: String

    fun onClear(user: User)

    companion object {
        private val quests = HashMap<String, Quest>()

        val list: List<Quest>
            get() = quests.values.toList()

        operator fun get(name: String) = quests[name]
        operator fun set(name: String, quest: Quest) {
            quests[name] = quest
        }
    }
}

class SimpleQuest(
    override val name: String,
    val type: QuestType,
    val requires: Int,
    val rewards: Int
): Quest {
    fun onIncrement(player: Player, current: Int) {
        player.sendMessage(Component.text("$name: $current/$requires", NamedTextColor.GREEN))
    }

    override fun onClear(user: User) {
        user.level.addExperience(rewards, user)
    }
}

enum class QuestType {
    DAILY, WEEKLY, SPECIAL
}

/*
quest:
{_id, uuid, name, count}

clear => delete
register => insert(grant)

== == == == == == == == == == == ==
quest.yml

daily:
  ZOMBIE:
    require: x
    reward: y
  SKELETON:
    require: x`
    reward: y`
  ...
special:
  rare mobs...

week:
  boss...
 */