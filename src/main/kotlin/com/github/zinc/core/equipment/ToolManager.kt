package com.github.zinc.core.equipment

import com.github.zinc.core.player.StatusType
import com.github.zinc.util.extension.getPersistent
import com.github.zinc.util.extension.setPersistent
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

object ToolManager {
    private const val WOOD = "WOODEN"
    private const val STONE = "STONE"
    private const val IRON = "IRON"
    private const val GOLD = "GOLDEN"
    private const val DIAMOND = "DIAMOND"
    private const val NETHERITE = "NETHERITE"

    private const val PICKAXE ="PICKAXE"
    private const val AXE = "AXE"
    private const val SWORD = "SWORD"
    private const val BOW = "BOW"
    private const val CROSSBOW = "CROSSBOW"
    private const val TRIDENT = "TRIDENT"
    private const val SHIELD = "SHIELD"

    private const val HELMET = "HELMET"
    private const val CHESTPLATE = "CHESTPLATE"
    private const val LEGGINGS = "LEGGINGS"
    private const val BOOTS = "BOOTS"
    private const val FISHING_ROD = "FISHING_ROD"

    val STATUS_KEY = NamespacedKey.minecraft("status_added")
    val STRENGTH = NamespacedKey.minecraft("str")
    val SWIFTNESS = NamespacedKey.minecraft("swt")
    val BALANCE = NamespacedKey.minecraft("bal")
    val CONCENTRATION = NamespacedKey.minecraft("con")
    private val LEVEL_CONSTRAINT_KEY = NamespacedKey.minecraft("level_constraint")

    internal fun ItemStack.isTool(): Boolean {
        return this.type.name.let {
            it.contains(PICKAXE)    || it.contains(AXE)         || it.contains(SWORD)       ||
            it.contains(BOW)        || it.contains(CROSSBOW)    || it.contains(TRIDENT)     ||
            it.contains(SHIELD)     || it.contains(HELMET)      || it.contains(CHESTPLATE)  ||
            it.contains(LEGGINGS)   || it.contains(BOOTS)       || it.contains(FISHING_ROD)
        }
    }

    /**
     * netherite tools = max 200 / bow is con 150 / crossbow is con 75 str 75
     * shield is bal 100
     * fullstack tools = max 400 / bow is 500.
     * ex) 보호4 내구3 네라 갑옷 : bal 400
     *     날카5 화염2 네라 검 : str 200 swt 200
     *     효율5 내구3 네라 곡괭이 : bal 300 str 100
     *     날카5 내구3 네라 도끼 : str 300 bal 100
     *     힘5 무한 화염 활 : con 400 str 100
     *     관통4 빠른장전3 쇠뇌 : con 200 str 200
     *     충절3
     */
    fun getRequireStatus(itemStack: ItemStack): Map<StatusType, Int> {
        var str = 0
        var swt = 0
        var bal = 0
        var con = 0
        var amplifier = 1.0
        val name = itemStack.type.name

        //set Material && Tool
        when {
            name.contains(BOW) && !name.contains("CROSS")-> {
                con = 150
            }
            name.contains(CROSSBOW) -> {
                str = 70
                con = 70
            }
            name.contains(SHIELD) -> {
                bal = 100
            }
            name.contains(TRIDENT) -> {
                con = 30
                str = 30
                bal = 30
            }
            name.contains(FISHING_ROD) -> {
                con = 30
                bal = 30
            }

            //General
            else -> {
                 amplifier = when {
                    name.contains(WOOD) -> .0
                    name.contains(STONE) -> .5
                    name.contains(GOLD) -> .5
                    name.contains(IRON) -> 1.0
                    name.contains(DIAMOND) -> 1.5
                    name.contains(NETHERITE) -> 2.5
                    else -> return mapOf()
                }
                when {
                    //tools
                    name.contains(PICKAXE) -> {
                        bal = 60
                        str = 20
                    }
                    name.contains(AXE) && !name.contains("PICK") -> {
                        str = 60
                        bal = 20
                    }
                    name.contains(SWORD) -> {
                        swt = 60
                        str = 20
                    }

                    //Equipments
                    name.contains(HELMET) -> {
                        bal = 60
                        con = 20
                    }
                    name.contains(CHESTPLATE) -> {
                        bal = 80
                    }
                    name.contains(LEGGINGS) -> {
                        bal = 40
                        str = 40
                    }
                    name.contains(BOOTS) -> {
                        bal = 40
                        swt = 40
                    }
                }
            }
        }

        //set enchants
        for(enchantEntry in itemStack.enchantments) {
            val enchant = enchantEntry.key
            val level = enchantEntry.value

            when(enchant) {
                //All Eqs
                Enchantment.PROTECTION_ENVIRONMENTAL -> bal += 20 * level
                Enchantment.PROTECTION_FIRE -> bal += 10 * level
                Enchantment.PROTECTION_EXPLOSIONS -> bal += 10 * level
                Enchantment.PROTECTION_PROJECTILE -> bal += 10 * level
                Enchantment.THORNS -> str += 15 * level

                //Helmet
                Enchantment.WATER_WORKER -> con += 10 * level

                //leggings
                Enchantment.SWIFT_SNEAK -> swt += 10 * level

                //Boots
                Enchantment.PROTECTION_FALL -> swt += 10 * level
                Enchantment.DEPTH_STRIDER -> swt += 10 * level
                Enchantment.FROST_WALKER -> swt += 10 * level
                Enchantment.SOUL_SPEED -> swt += 10 * level

                //damaging
                Enchantment.DAMAGE_ALL -> str += 20 * level
                Enchantment.DAMAGE_UNDEAD -> str += 20 * level
                Enchantment.DAMAGE_ARTHROPODS -> str += 20 * level
                Enchantment.SWEEPING_EDGE -> str += 10 * level

                //effects
                Enchantment.KNOCKBACK -> str += 10 * level
                Enchantment.FIRE_ASPECT -> con += 20 * level
                Enchantment.LOOT_BONUS_MOBS -> itemStack.setLevelConstraint(50 * level)

                //tools
                Enchantment.DIG_SPEED -> {
                    bal += 10 * level
                    str += 10 * level
                }
                Enchantment.SILK_TOUCH -> con += 30
                Enchantment.DURABILITY -> itemStack.setLevelConstraint(50 * level)
                Enchantment.LOOT_BONUS_BLOCKS -> itemStack.setLevelConstraint(60 * level)

                //bow
                Enchantment.ARROW_DAMAGE -> {
                    str += 10 * level
                    con += 30 * level
                }
                Enchantment.ARROW_KNOCKBACK -> str += 10 * level
                Enchantment.ARROW_FIRE -> con += 20
                Enchantment.ARROW_INFINITE -> itemStack.setLevelConstraint(180)

                //fishing rod
                Enchantment.LUCK -> itemStack.addLevelConstraint(40 * level)
                Enchantment.LURE -> itemStack.addLevelConstraint(40 * level)

                //trident
                Enchantment.LOYALTY -> con += 30 * level
                Enchantment.IMPALING -> str += 20 * level
                Enchantment.RIPTIDE -> {
                    swt += 30 * level
                    itemStack.addLevelConstraint(50 * level)
                }
                Enchantment.CHANNELING -> itemStack.addLevelConstraint(120)

                //crossbow
                Enchantment.MULTISHOT -> {
                    con += 30
                    str += 30
                }
                Enchantment.QUICK_CHARGE -> {
                    con += 30 * level
                    bal += 20 * level
                }
                Enchantment.PIERCING -> str += 20 * level

                //mending
                Enchantment.MENDING -> itemStack.setLevelConstraint(250)
            }
        }

        return mapOf(
            StatusType.STRENGTH to (str * amplifier).toInt(),
            StatusType.SWIFTNESS to (swt * amplifier).toInt(),
            StatusType.BALANCE to (bal * amplifier).toInt(),
            StatusType.CONCENTRATION to (con * amplifier).toInt()
        )
    }

    private fun ItemStack.setLevelConstraint(level: Int) {
        val constraint = this.getPersistent(LEVEL_CONSTRAINT_KEY, PersistentDataType.INTEGER) ?: return
        if(constraint < level) this.setPersistent(LEVEL_CONSTRAINT_KEY, level, PersistentDataType.INTEGER)
    }

    private fun ItemStack.addLevelConstraint(level: Int) {
        val constraint = this.getPersistent(LEVEL_CONSTRAINT_KEY)?.toInt() ?: 0
        this.setPersistent(LEVEL_CONSTRAINT_KEY, level + constraint, PersistentDataType.INTEGER)
    }
}