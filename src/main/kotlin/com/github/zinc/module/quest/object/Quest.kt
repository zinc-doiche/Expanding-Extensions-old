package com.github.zinc.module.quest.`object`

import com.github.zinc.module.user.`object`.User
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import java.util.EnumMap

interface Quest {
    val name: String

    fun onClear(user: User)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleQuest

        if (name != other.name) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "SimpleQuest(name='$name', type=$type, requires=$requires, rewards=$rewards)"
    }

    companion object {
        private val quests = HashMap<String, SimpleQuest>()

        val list: List<SimpleQuest>
            get() = quests.values.toList()

        operator fun get(name: String) = quests[name]
        operator fun set(name: String, quest: SimpleQuest) {
            quests[name] = quest
        }
    }

    object Container {
        private var dailyQuests: MutableMap<String, SimpleQuest> = HashMap()
        private var weeklyQuests: MutableMap<String, SimpleQuest> = HashMap()

        operator fun get(type: QuestType) = when(type) {
            QuestType.DAILY -> dailyQuests
            QuestType.WEEKLY -> weeklyQuests
        }

        operator fun set(type: QuestType, quests: MutableMap<String, SimpleQuest>) = when(type) {
            QuestType.DAILY -> dailyQuests = quests
            QuestType.WEEKLY -> weeklyQuests = quests
        }
    }
}

enum class QuestType {
    DAILY, WEEKLY
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