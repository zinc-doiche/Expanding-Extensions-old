package com.github.zinc.module.quest.`object`

import com.github.zinc.module.user.`object`.User

interface Quest {
    val name: String

    fun onClear(user: User)

    companion object {
        private val quests = HashMap<String, Quest>()

        operator fun get(name: String) = quests[name]
        operator fun set(name: String, quest: Quest) {
            quests[name] = quest
        }
    }
}

class SimpleQuest(
    override val name: String,
    val requires: Int,
    val rewards: Int
): Quest {
    override fun onClear(user: User) {
        user.level.addExperience(rewards, user)
    }
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