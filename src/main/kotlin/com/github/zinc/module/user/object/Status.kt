package com.github.zinc.module.user.`object`

import com.github.zinc.core.equipment.LEVEL_CONSTRAINT_KEY
import com.github.zinc.util.getPersistent
import com.github.zinc.util.hasPersistent
import com.github.zinc.util.setPersistent
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

data class Status(
    var strength: Int = 0,
    var swiftness: Int = 0,
    var balance: Int = 0,
    var concentration: Int = 0,
    var remains: Int = 0
) {
    val total: Int
        get() = strength + swiftness + balance + concentration + remains

    operator fun times(times: Double)
            = Status((strength * times).toInt(), (swiftness * times).toInt(), (balance * times).toInt(), (concentration * times).toInt())

    private fun clear() {
        strength = 0
        swiftness = 0
        balance = 0
        concentration = 0
        remains = 0
    }

    fun add(status: Status) {
        strength += status.strength
        swiftness += status.swiftness
        balance += status.balance
        concentration += status.concentration
    }

    //=========================================================================================

    fun setStatus(itemStack: ItemStack) {
        //prevent status from multiply
        clear()

        itemStack.type.name.split('_').run {
            // info(this)

            //enchants
            itemStack.itemMeta.enchants.entries.forEach{
                enchantMap[it.key]?.let { constraint ->
                    when(constraint) {
                        is Status -> add(constraint)
                        is Pair<*, *> -> {
                            add(constraint.first as Status)
                            itemStack.addLevelConstraint(constraint.second as Int)
                        }
                        is Int -> itemStack.addLevelConstraint(constraint)
                    }
                }
            }

            when(size) {
                //NON-TYPED tools
                1 -> toolMap[itemStack.type.name]?.let(::add) ?: run {
                    if(itemStack.type.name == ELYTRA) itemStack.addLevelConstraint(200)
                    return
                }

                //TYPED tools
                2 -> {
                    if(get(0) == "FISHING") {
                        concentration = 30
                        balance = 30
                        return
                    }

                    val amplifier: Double = materialMap[get(0)] ?: return
                    val constraint = typedToolMap[get(1)]?.times(amplifier) ?: equipmentMap[get(1)]?.times(amplifier) ?: return

                    add(constraint)
                }
            }
        }
    }

    override fun toString(): String {
        return "Status(strength=$strength, swiftness=$swiftness, balance=$balance, concentration=$concentration, remains=$remains)"
    }

    companion object {
        const val WOOD = "WOODEN"
        const val STONE = "STONE"
        const val IRON = "IRON"
        const val GOLD = "GOLDEN"
        const val DIAMOND = "DIAMOND"
        const val NETHERITE = "NETHERITE"

        private val materialMap: Map<String, Double> = mapOf(WOOD to .0, STONE to .2, IRON to 1.0, GOLD to .5, DIAMOND to 1.5, NETHERITE to 2.5)

        const val BOW = "BOW"
        const val CROSSBOW = "CROSSBOW"
        const val TRIDENT = "TRIDENT"
        const val SHIELD = "SHIELD"
        const val FISHING_ROD = "FISHING_ROD"
        const val ELYTRA = "ELYTRA"

        private val toolMap: Map<String, Status> = mapOf(
            BOW to Status(concentration = 150),
            CROSSBOW to Status(strength = 70, concentration = 70),
            TRIDENT to Status(concentration = 30, strength = 30, balance = 30),
            SHIELD to Status(balance = 100),
            FISHING_ROD to Status(concentration = 30, balance = 30),
        )

        const val PICKAXE ="PICKAXE"
        const val AXE = "AXE"
        const val SWORD = "SWORD"

        private val typedToolMap: Map<String, Status> = mapOf(
            PICKAXE to Status(balance = 60, strength = 20),
            AXE to Status(strength = 60, balance = 20),
            SWORD to Status(swiftness = 60, strength = 20),
        )

        const val HELMET = "HELMET"
        const val CHESTPLATE = "CHESTPLATE"
        const val LEGGINGS = "LEGGINGS"
        const val BOOTS = "BOOTS"

        private val equipmentMap: Map<String, Status> = mapOf(
            HELMET to Status(balance = 60, concentration = 20),
            CHESTPLATE to Status(balance = 80),
            LEGGINGS to Status(balance = 40, strength = 40),
            BOOTS to Status(balance = 40, swiftness = 40)
        )

        private val enchantMap: Map<Enchantment, Any> = mapOf(
            Enchantment.PROTECTION_ENVIRONMENTAL to Status(balance = 20),
            Enchantment.PROTECTION_FIRE to Status(balance = 10),
            Enchantment.PROTECTION_EXPLOSIONS to Status(balance = 10),
            Enchantment.PROTECTION_PROJECTILE to Status(balance = 10),
            Enchantment.THORNS to Status(strength = 15),

            //Helmet
            Enchantment.WATER_WORKER to Status(concentration = 10),

            //leggings
            Enchantment.SWIFT_SNEAK to Status(swiftness = 10),

            //Boots
            Enchantment.PROTECTION_FALL to Status(swiftness = 10),
            Enchantment.DEPTH_STRIDER to Status(swiftness = 10),
            Enchantment.FROST_WALKER to Status(swiftness = 10),
            Enchantment.SOUL_SPEED to Status(swiftness = 10),

            //damaging
            Enchantment.DAMAGE_ALL to Status(strength = 20),
            Enchantment.DAMAGE_UNDEAD to Status(strength = 20),
            Enchantment.DAMAGE_ARTHROPODS to Status(strength = 20),
            Enchantment.SWEEPING_EDGE to Status(strength = 10),

            //effects
            Enchantment.KNOCKBACK to Status(strength = 10),
            Enchantment.FIRE_ASPECT to Status(concentration = 20),
            Enchantment.LOOT_BONUS_MOBS to 50,

            //tools
            Enchantment.DIG_SPEED to Status(balance = 10, strength = 10),
            Enchantment.SILK_TOUCH to Status(concentration = 30),
            Enchantment.DURABILITY to 50,
            Enchantment.LOOT_BONUS_BLOCKS to 60,

            //bow
            Enchantment.ARROW_DAMAGE to Status(strength = 10, concentration = 30),
            Enchantment.ARROW_KNOCKBACK to Status(strength = 10),
            Enchantment.ARROW_FIRE to Status(concentration = 20),
            Enchantment.ARROW_INFINITE to 180,

            //fishing rod
            Enchantment.LUCK to 40,
            Enchantment.LURE to 40,

            //trident
            Enchantment.LOYALTY to Status(concentration = 30),
            Enchantment.IMPALING to Status(strength = 20),
            Enchantment.RIPTIDE to Pair(Status(swiftness = 30), 50),
            Enchantment.CHANNELING to 120,

            //crossbow
            Enchantment.MULTISHOT to Status(concentration = 30, strength = 30),
            Enchantment.QUICK_CHARGE to Status(concentration = 30, balance = 20),
            Enchantment.PIERCING to Status(strength = 20),

            //mending
            Enchantment.MENDING to 250
        )
    }
}

private fun ItemStack.setLevelConstraint(level: Int) {
    val constraint = if(this.hasPersistent(LEVEL_CONSTRAINT_KEY)) {
        this.getPersistent(LEVEL_CONSTRAINT_KEY, PersistentDataType.INTEGER)!!
    } else 0
    if(constraint < level) {
        this.setPersistent(LEVEL_CONSTRAINT_KEY, level, PersistentDataType.INTEGER)
    }
}

private fun ItemStack.addLevelConstraint(level: Int) {
    val constraint = if(this.hasPersistent(LEVEL_CONSTRAINT_KEY)) {
        this.getPersistent(LEVEL_CONSTRAINT_KEY, PersistentDataType.INTEGER)!!
    } else 0
    this.setPersistent(LEVEL_CONSTRAINT_KEY, level + constraint, PersistentDataType.INTEGER)
}