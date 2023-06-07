package com.github.zinc.core.equipment

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import kotlin.math.round

class ZincEquipment(
    private var equipment: ItemStack,
    var constraint: Status
) {
    constructor(equipment: ItemStack) : this(equipment, Status()) {

    }

}

data class Status(
    var strength: Int = 0,
    var swiftness: Int = 0,
    var balance: Int = 0,
    var concentration: Int = 0
) {
    constructor() : this(0,0,0,0)

    constructor(
        strength: Double = .0,
        swiftness: Double = .0,
        balance: Double = .0,
        concentration: Double = .0
    ) : this(strength.toInt(), swiftness.toInt(), balance.toInt(), concentration.toInt())

    operator fun times(times: Int)
        = Status(strength * times, swiftness * times, balance * times, concentration * times)

    fun setStatus(itemStack: ItemStack) {
        val name = itemStack.type.name

        itemStack.type.name.split('_').run {
            when(size) {
                1 -> toolMap[get(0)]?.let { status ->
                    this@Status.copy(
                        strength = status.strength,
                        swiftness = status.swiftness,
                        balance = status.balance,
                        concentration = status.concentration
                    )

                    itemStack.itemMeta.enchants.forEach {

                    }
                } ?: return
                2 -> {
                    if(get(0) == "FISHING") {
                        concentration = 30
                        balance = 30
                        return
                    }

                    val amplifier = materialMap[get(0)] ?: return

                    typedToolMap[get(1)]?.let { status ->
                        this@Status.copy(
                            strength = (status.strength * amplifier).toInt(),
                            swiftness = (status.swiftness * amplifier).toInt(),
                            balance = (status.balance * amplifier).toInt(),
                            concentration = (status.concentration * amplifier).toInt()
                        )
                    } ?: return
                }
                else -> return
            }
        }
    }
}

private const val WOOD = "WOODEN"
private const val STONE = "STONE"
private const val IRON = "IRON"
private const val GOLD = "GOLDEN"
private const val DIAMOND = "DIAMOND"
private const val NETHERITE = "NETHERITE"

private val materialMap = mapOf(WOOD to .0, STONE to .2, IRON to 1.0, GOLD to .5, DIAMOND to 1.5, NETHERITE to 2.5)

private const val BOW = "BOW"
private const val CROSSBOW = "CROSSBOW"
private const val TRIDENT = "TRIDENT"
private const val SHIELD = "SHIELD"
private const val FISHING_ROD = "FISHING_ROD"

private val toolMap = mapOf(
    BOW to Status(concentration = 150),
    CROSSBOW to Status(strength = 70, concentration = 70),
    TRIDENT to Status(concentration = 30, strength = 30, balance = 30),
    SHIELD to Status(balance = 100),
    FISHING_ROD to Status(concentration = 30, balance = 30)
)

private const val PICKAXE ="PICKAXE"
private const val AXE = "AXE"
private const val SWORD = "SWORD"

private val typedToolMap = mapOf(
    PICKAXE to Status(balance = 60, strength = 20),
    AXE to Status(strength = 60, balance = 20),
    SWORD to Status(swiftness = 60, strength = 20)
)

private const val HELMET = "HELMET"
private const val CHESTPLATE = "CHESTPLATE"
private const val LEGGINGS = "LEGGINGS"
private const val BOOTS = "BOOTS"

private val equipmentMap = mapOf(
    HELMET to Status(balance = 60, concentration = 20),
    CHESTPLATE to Status(balance = 80),
    LEGGINGS to Status(balance = 40, strength = 40),
    BOOTS to Status(balance = 40, swiftness = 40)
)

val STATUS_KEY = NamespacedKey.minecraft("status_added")
val STRENGTH = NamespacedKey.minecraft("str")
val SWIFTNESS = NamespacedKey.minecraft("swt")
val BALANCE = NamespacedKey.minecraft("bal")
val CONCENTRATION = NamespacedKey.minecraft("con")
private val LEVEL_CONSTRAINT_KEY = NamespacedKey.minecraft("level_constraint")

internal fun ItemStack.isEquipment(): Boolean {
    return this.type.name.let {
        it.contains(HELMET)     || it.contains(CHESTPLATE) ||
        it.contains(LEGGINGS)   || it.contains(BOOTS)
    }
}

internal fun ItemStack.isTool(): Boolean {
    return this.type.name.let {
        it.contains(PICKAXE)    || it.contains(AXE)         || it.contains(SWORD)       ||
        it.contains(BOW)        || it.contains(CROSSBOW)    || it.contains(TRIDENT)     ||
        it.contains(SHIELD)     || it.contains(FISHING_ROD)
    } || this.isEquipment()
}
