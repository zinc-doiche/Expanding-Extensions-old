package com.github.zinc.module.user.`object`

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class User(
    val uuid: String,
    val status: Status = Status(),
    val level: Level = Level(),
    val trinket: Trinket = Trinket(),
) {

    val player: Player?
        get() = Bukkit.getPlayer(UUID.fromString(uuid))

    val name: String?
        get() = player?.name

    companion object {
        private val users: HashMap<String, User> = HashMap()

        operator fun get(uuid: String): User? = users[uuid]
        operator fun set(uuid: String, user: User) {
            users[uuid] = user
        }
        operator fun contains(uuid: String): Boolean = users.containsKey(uuid)

        fun getPlayer(uuid: String): Player? = Bukkit.getPlayer(UUID.fromString(uuid))
    }
}