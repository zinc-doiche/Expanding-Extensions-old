package com.github.zinc.module.user.`object`

import com.github.zinc.core.equipment.LEVEL_CONSTRAINT_KEY
import com.github.zinc.util.getPersistent
import com.github.zinc.util.hasPersistent
import com.github.zinc.util.setPersistent
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class Status(
    strength: Int = 0,
    swiftness: Int = 0,
    balance: Int = 0,
    concentration: Int = 0
) {
    var strength: Int = strength
        private set
    var swiftness: Int = swiftness
        private set
    var balance: Int = balance
        private set
    var concentration: Int = concentration
        private set
    var remains: Int = 0
        private set
    val total: Int
        get() = strength + swiftness + balance + concentration + remains

    operator fun times(times: Double)
            = Status((strength * times).toInt(), (swiftness * times).toInt(), (balance * times).toInt(), (concentration * times).toInt())

    operator fun get(type: StatusType): Int {
        return when(type) {
            StatusType.STRENGTH -> strength
            StatusType.SWIFTNESS -> swiftness
            StatusType.BALANCE -> balance
            StatusType.CONCENTRATION -> concentration
            StatusType.REMAIN -> remains
        }
    }

    fun checkAddition(type: StatusType, amount: Int): Boolean {
        return when(type) {
            StatusType.STRENGTH -> strength + amount >= 0
            StatusType.SWIFTNESS -> swiftness + amount >= 0
            StatusType.BALANCE -> balance + amount >= 0
            StatusType.CONCENTRATION -> concentration + amount >= 0
            StatusType.REMAIN -> remains + amount >= 0
        }
    }

    fun checkDistribution(type: StatusType, amount: Int): Boolean {
        return when (type) {
            StatusType.STRENGTH -> remains >= amount
            StatusType.SWIFTNESS -> remains >= amount
            StatusType.BALANCE -> remains >= amount
            StatusType.CONCENTRATION -> remains >= amount
            StatusType.REMAIN -> true
        }
    }

    fun distribute(type: StatusType, amount: Int = 1) {
        when(type) {
            StatusType.STRENGTH -> {
                strength += amount
                remains -= amount
            }
            StatusType.SWIFTNESS -> {
                swiftness += amount
                remains -= amount
            }
            StatusType.BALANCE -> {
                balance += amount
                remains -= amount
            }
            StatusType.CONCENTRATION -> {
                concentration += amount
                remains -= amount
            }
            StatusType.REMAIN -> return
        }
    }

    fun addRemains(amount: Int) {
        remains += amount
    }

    fun addStatus(type: StatusType, amount: Int): Int {
        return when(type) {
            StatusType.STRENGTH -> {
                strength += amount
                strength
            }
            StatusType.SWIFTNESS -> {
                swiftness += amount
                swiftness
            }
            StatusType.BALANCE -> {
                balance += amount
                balance
            }
            StatusType.CONCENTRATION -> {
                concentration += amount
                concentration
            }
            StatusType.REMAIN -> {
                addRemains(amount)
                remains
            }
        }
    }

    fun applyStatus(player: Player) {
        val damageAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) ?: return
        val healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
        val speedAttribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return

        damageAttribute.baseValue = damage
        healthAttribute.baseValue = health
        speedAttribute.baseValue = speed
        User[player]?.updateCriticalChance()
    }

    private val damage: Double
        get() = 1 + when(strength) {
            // 5/150 ps
            in 0..INTERVAL1 -> //150
                strength * 5.0 / INTERVAL1 //max 6 = 1 + 5
            // 3/150 ps
            in INTERVAL1 + 1..INTERVAL2 -> //250
                6 + (strength - INTERVAL1) * 2.0 / (INTERVAL2 - INTERVAL1) //max 8 = 1 + 5 + 2
            // 1/150 ps
            in INTERVAL2 + 1..INTERVAL3 -> //400
                8 + (strength - INTERVAL2) * 1.0 / (INTERVAL3 - INTERVAL2) //max 9 = 1 + 5 + 2 + 1
            // 3/150 ps
            in INTERVAL3 + 1..INTERVAL4 -> //550
                9 + (strength - INTERVAL3) * 2.0 / (INTERVAL4 - INTERVAL3) //max 11 = 1 + 5 + 2 + 1 + 2
            // 12/150 ps
            else -> //600
                11 + (strength - INTERVAL4) * 4.0 / (600 - INTERVAL4) //max 15 = 1 + 5 + 2 + 1 + 2 + 4
        }

    private val health: Double
        get() = 20 + when(balance) {
            // 10/150 ps
            in 0..INTERVAL1 -> //150
                balance * 10.0 / INTERVAL1 //max 30 = 20 + 10
            // 9/150 ps
            in INTERVAL1 + 1..INTERVAL2 -> //250
                10 + (balance - INTERVAL1) * 6.0 / (INTERVAL2 - INTERVAL1) //max 36 = 20 + 10 + 6
            // 4/150 ps
            in INTERVAL2 + 1..INTERVAL3 -> //400
                16 + (balance - INTERVAL2) * 4.0 / (INTERVAL3 - INTERVAL2) //max 40 = 20 + 10 + 6 + 4
            // 6/150 ps
            in INTERVAL3 + 1..INTERVAL4 -> //550
                20 + (balance - INTERVAL3) * 6.0 / (INTERVAL4 - INTERVAL3) //max 46 = 20 + 10 + 6 + 4 + 6
            // 42/150 ps
            else -> //600
                26 + (balance - INTERVAL4) * 14.0 / (600 - INTERVAL4) //max 60 = 20 + 10 + 6 + 4 + 6 + 14
        }


    private val speed: Double
        get() = 0.1 + when(swiftness) {
            // 0.04/150 ps
            in 0..INTERVAL1 -> //150
                swiftness * .04 / INTERVAL1 //max 0.14 = .1 + .04
            // 0.03/150 ps
            in INTERVAL1 + 1..INTERVAL2 -> //250
                .14 + (swiftness - INTERVAL1) * .02 / (INTERVAL2 - INTERVAL1) //max 0.16 = 0.1 + .04 + .02
            // 0.02/150 ps
            in INTERVAL2 + 1..INTERVAL3 -> //400
                .16 + (swiftness - INTERVAL2) * .02 / (INTERVAL3 - INTERVAL2) //max 0.18 = 0.1 + .04 + .02 + .02
            // 0.04/150 ps
            in INTERVAL3 + 1..INTERVAL4 -> //550
                .18 + (swiftness - INTERVAL3) * .04 / (INTERVAL4 - INTERVAL3) //max 0.22 = 0.1 + .04 + .02 + .02 + .04
            // 0.24/150 ps
            else -> //600
                .22 + (swiftness - INTERVAL4) * .08 / (600 - INTERVAL4) //max 0.3 = 0.1 + .04 + .02 + .02 + .04 + .08
        }

    //활이 메인딜
    val criticalChance: Double
        get() = when(concentration) {
            // 20/15000 ps
            in 0..INTERVAL1 -> //150
                concentration * .2 / INTERVAL1 //max 0.2 = 0 + .2
            // 5/15000 ps
            in INTERVAL1 + 1..INTERVAL2 -> //250
                .2 + (concentration - INTERVAL1) * .05 / (INTERVAL2 - INTERVAL1) //max 0.25 = 0 + .2 + .05
            // 5/15000 ps
            in INTERVAL2 + 1..INTERVAL3 -> //400
                .25 + (concentration - INTERVAL2) * .05 / (INTERVAL3 - INTERVAL2) //max 0.3 = 0 + .2 + .05 +.05
            // 10/15000 ps
            in INTERVAL3 + 1..INTERVAL4 -> //550
                .3 + (concentration - INTERVAL3) * .1 / (INTERVAL4 - INTERVAL3) //max 0.4 = 0 + .2 + .05 + .05 + .1
            // 60/15000 ps
            else -> //600
                .4 + (concentration - INTERVAL4) * .6 / (600 - INTERVAL4) //max 1 = 0 + .2 + .05 + .05 + .1 + .6
        }

//    fun add(status: Status) {
//        strength += status.strength
//        swiftness += status.swiftness
//        balance += status.balance
//        concentration += status.concentration
//    }
//
//    private fun clear() {
//        strength = 0
//        swiftness = 0
//        balance = 0
//        concentration = 0
//        remains = 0
//    }
//
//    fun setStatus(itemStack: ItemStack) {
//        //prevent status from multiply
//        clear()
//
//        itemStack.type.name.split('_').run {
//            // info(this)
//
//            //enchants
//            itemStack.itemMeta.enchants.entries.forEach{
//                enchantMap[it.key]?.let { constraint ->
//                    when(constraint) {
//                        is Status -> add(constraint)
//                        is Pair<*, *> -> {
//                            add(constraint.first as Status)
//                            itemStack.addLevelConstraint(constraint.second as Int)
//                        }
//                        is Int -> itemStack.addLevelConstraint(constraint)
//                    }
//                }
//            }
//
//            when(size) {
//                //NON-TYPED tools
//                1 -> toolMap[itemStack.type.name]?.let(::add) ?: run {
//                    if(itemStack.type.name == ELYTRA) itemStack.addLevelConstraint(200)
//                    return
//                }
//
//                //TYPED tools
//                2 -> {
//                    if(get(0) == "FISHING") {
//                        concentration = 30
//                        balance = 30
//                        return
//                    }
//
//                    val amplifier: Double = materialMap[get(0)] ?: return
//                    val constraint = typedToolMap[get(1)]?.times(amplifier) ?: equipmentMap[get(1)]?.times(amplifier) ?: return
//
//                    add(constraint)
//                }
//            }
//        }
//    }

    override fun toString(): String {
        return "Status(strength=$strength, swiftness=$swiftness, balance=$balance, concentration=$concentration, remains=$remains)"
    }

    companion object {
        private const val INTERVAL1 = 150
        private const val INTERVAL2 = 250
        private const val INTERVAL3 = 400
        private const val INTERVAL4 = 550

//        const val WOOD = "WOODEN"
//        const val STONE = "STONE"
//        const val IRON = "IRON"
//        const val GOLD = "GOLDEN"
//        const val DIAMOND = "DIAMOND"
//        const val NETHERITE = "NETHERITE"
//
//        private val materialMap: Map<String, Double> = mapOf(WOOD to .0, STONE to .2, IRON to 1.0, GOLD to .5, DIAMOND to 1.5, NETHERITE to 2.5)
//
//        const val BOW = "BOW"
//        const val CROSSBOW = "CROSSBOW"
//        const val TRIDENT = "TRIDENT"
//        const val SHIELD = "SHIELD"
//        const val FISHING_ROD = "FISHING_ROD"
//        const val ELYTRA = "ELYTRA"
//
//        private val toolMap: Map<String, Status> = mapOf(
//            BOW to Status(concentration = 150),
//            CROSSBOW to Status(strength = 70, concentration = 70),
//            TRIDENT to Status(concentration = 30, strength = 30, balance = 30),
//            SHIELD to Status(balance = 100),
//            FISHING_ROD to Status(concentration = 30, balance = 30),
//        )
//
//        const val PICKAXE ="PICKAXE"
//        const val AXE = "AXE"
//        const val SWORD = "SWORD"
//
//        private val typedToolMap: Map<String, Status> = mapOf(
//            PICKAXE to Status(balance = 60, strength = 20),
//            AXE to Status(strength = 60, balance = 20),
//            SWORD to Status(swiftness = 60, strength = 20),
//        )
//
//        const val HELMET = "HELMET"
//        const val CHESTPLATE = "CHESTPLATE"
//        const val LEGGINGS = "LEGGINGS"
//        const val BOOTS = "BOOTS"
//
//        private val equipmentMap: Map<String, Status> = mapOf(
//            HELMET to Status(balance = 60, concentration = 20),
//            CHESTPLATE to Status(balance = 80),
//            LEGGINGS to Status(balance = 40, strength = 40),
//            BOOTS to Status(balance = 40, swiftness = 40)
//        )
//
//        private val enchantMap: Map<Enchantment, Any> = mapOf(
//            Enchantment.PROTECTION_ENVIRONMENTAL to Status(balance = 20),
//            Enchantment.PROTECTION_FIRE to Status(balance = 10),
//            Enchantment.PROTECTION_EXPLOSIONS to Status(balance = 10),
//            Enchantment.PROTECTION_PROJECTILE to Status(balance = 10),
//            Enchantment.THORNS to Status(strength = 15),
//
//            //Helmet
//            Enchantment.WATER_WORKER to Status(concentration = 10),
//
//            //leggings
//            Enchantment.SWIFT_SNEAK to Status(swiftness = 10),
//
//            //Boots
//            Enchantment.PROTECTION_FALL to Status(swiftness = 10),
//            Enchantment.DEPTH_STRIDER to Status(swiftness = 10),
//            Enchantment.FROST_WALKER to Status(swiftness = 10),
//            Enchantment.SOUL_SPEED to Status(swiftness = 10),
//
//            //damaging
//            Enchantment.DAMAGE_ALL to Status(strength = 20),
//            Enchantment.DAMAGE_UNDEAD to Status(strength = 20),
//            Enchantment.DAMAGE_ARTHROPODS to Status(strength = 20),
//            Enchantment.SWEEPING_EDGE to Status(strength = 10),
//
//            //effects
//            Enchantment.KNOCKBACK to Status(strength = 10),
//            Enchantment.FIRE_ASPECT to Status(concentration = 20),
//            Enchantment.LOOT_BONUS_MOBS to 50,
//
//            //tools
//            Enchantment.DIG_SPEED to Status(balance = 10, strength = 10),
//            Enchantment.SILK_TOUCH to Status(concentration = 30),
//            Enchantment.DURABILITY to 50,
//            Enchantment.LOOT_BONUS_BLOCKS to 60,
//
//            //bow
//            Enchantment.ARROW_DAMAGE to Status(strength = 10, concentration = 30),
//            Enchantment.ARROW_KNOCKBACK to Status(strength = 10),
//            Enchantment.ARROW_FIRE to Status(concentration = 20),
//            Enchantment.ARROW_INFINITE to 180,
//
//            //fishing rod
//            Enchantment.LUCK to 40,
//            Enchantment.LURE to 40,
//
//            //trident
//            Enchantment.LOYALTY to Status(concentration = 30),
//            Enchantment.IMPALING to Status(strength = 20),
//            Enchantment.RIPTIDE to Pair(Status(swiftness = 30), 50),
//            Enchantment.CHANNELING to 120,
//
//            //crossbow
//            Enchantment.MULTISHOT to Status(concentration = 30, strength = 30),
//            Enchantment.QUICK_CHARGE to Status(concentration = 30, balance = 20),
//            Enchantment.PIERCING to Status(strength = 20),
//
//            //mending
//            Enchantment.MENDING to 250
//        )
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