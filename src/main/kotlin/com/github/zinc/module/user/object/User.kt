package com.github.zinc.module.user.`object`

import com.github.zinc.module.item.`object`.trinket.Trinket
import com.github.zinc.module.item.`object`.trinket.TrinketSlot
import com.github.zinc.module.quest.`object`.Quest
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class User(
    val uuid: String,
    val status: Status = Status(),
    val level: Level = Level(),
    val questProcesses: HashMap<String, Int> = HashMap()
) {
    @Transient
    lateinit var trinkets: Map<TrinketSlot, Trinket>
        private set

    @Transient
    var criticalChance: Double = .0
        private set

    init {
        init()
    }

    val player: Player?
        get() = Bukkit.getPlayer(UUID.fromString(uuid))

    val name: String?
        get() = player?.name

    fun init() {
        trinkets = EnumMap(TrinketSlot::class.java)
    }

    fun questIncrement(name: String): Int {
        val current = questProcesses[name] ?: return -1
        questProcesses[name] = current + 1
        return current + 1
    }

    fun updateCriticalChance() {
        criticalChance = status.criticalChance
    }

    fun setTrinket(trinket: Trinket) {
        (trinkets as MutableMap)[trinket.slot] = trinket
    }

    fun removeTrinket(slot: TrinketSlot) {
        (trinkets as MutableMap).remove(slot)
    }

    fun sendMessage(message: Component) {
        player?.sendMessage(message)
    }

    override fun toString(): String {
        return "User(uuid='$uuid', status=$status, level=$level, trinkets=$trinkets)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        return uuid == other.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    companion object {
        @Transient
        private val users: HashMap<String, User> = HashMap()

        fun getUsers() = users.values.toList()

        operator fun get(uuid: String): User? = users[uuid]
        operator fun get(player: Player): User? = users[player.uniqueId.toString()]
        operator fun set(uuid: String, user: User) {
            users[uuid] = user
        }
        operator fun contains(uuid: String): Boolean = users.containsKey(uuid)
        fun remove(uuid: String) = users.remove(uuid)

        fun getPlayer(uuid: String): Player? = Bukkit.getPlayer(UUID.fromString(uuid))
    }
}

internal val Player.user: User?
    get() = User[uniqueId.toString()]

internal val Player.speed: Double
    get() = getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.value ?: -1.0

internal val Player.damage: Double
    get() = getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.value ?: -1.0