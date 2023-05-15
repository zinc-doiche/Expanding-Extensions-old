package com.github.zinc.core.equipment

import com.github.zinc.core.player.StatusType
import com.github.zinc.util.extension.hasPersistent
import com.github.zinc.util.extension.item
import com.github.zinc.util.extension.setPersistent
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
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

    private val STATUS_KEY = NamespacedKey.minecraft("status_added")

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
        val statusMap: EnumMap<StatusType, Int> = EnumMap(StatusType::class.java)
        val name = itemStack.type.name

        if(!itemStack.hasPersistent(STATUS_KEY)) return statusMap
        itemStack.setPersistent(STATUS_KEY, "status")

        when {
            name.contains(BOW) && !name.contains("CROSS")-> {
                statusMap[StatusType.CONCENTRATION] = 150
            }
            name.contains(CROSSBOW) -> {
                statusMap[StatusType.STRENGTH] = 70
                statusMap[StatusType.CONCENTRATION] = 70
            }
            name.contains(SHIELD) -> {
                statusMap[StatusType.BALANCE] = 100
            }
            name.contains(TRIDENT) -> {
                statusMap[StatusType.CONCENTRATION] = 30
                statusMap[StatusType.STRENGTH] = 30
                statusMap[StatusType.BALANCE] = 30
            }
            else -> {
                val amplifier = when {
                    name.contains(WOOD) -> .0
                    name.contains(STONE) -> .5
                    name.contains(GOLD) -> .5
                    name.contains(IRON) -> 1.0
                    name.contains(DIAMOND) -> 1.5
                    name.contains(NETHERITE) -> 2.5
                    else -> return statusMap
                }
                when {
                    name.contains(PICKAXE) -> {
                        statusMap[StatusType.BALANCE] = (amplifier * 60).toInt()
                        statusMap[StatusType.STRENGTH] = (amplifier * 20).toInt()
                    }
                    name.contains(AXE) && !name.contains("PICK") -> {
                        statusMap[StatusType.STRENGTH] = (amplifier * 60).toInt()
                        statusMap[StatusType.BALANCE] = (amplifier * 20).toInt()
                    }
                    name.contains(SWORD) -> {
                        statusMap[StatusType.SWIFTNESS] = (amplifier * 60).toInt()
                        statusMap[StatusType.STRENGTH] = (amplifier * 20).toInt()
                    }
                }
            }
        }

        for(enchantEntry in itemStack.enchantments) {
            val enchant = enchantEntry.key
            val level = enchantEntry.value

            when(enchant) {
                Enchantment.PROTECTION_ENVIRONMENTAL -> TODO()
                Enchantment.PROTECTION_FIRE -> TODO()
                Enchantment.PROTECTION_FALL -> TODO()
                Enchantment.PROTECTION_EXPLOSIONS -> TODO()
                Enchantment.PROTECTION_PROJECTILE -> TODO()
                Enchantment.OXYGEN -> TODO()
                Enchantment.WATER_WORKER -> TODO()
                Enchantment.THORNS -> TODO()
                Enchantment.DEPTH_STRIDER -> TODO()
                Enchantment.FROST_WALKER -> TODO()
                Enchantment.BINDING_CURSE -> TODO()
                Enchantment.DAMAGE_ALL -> TODO()
                Enchantment.DAMAGE_UNDEAD -> TODO()
                Enchantment.DAMAGE_ARTHROPODS -> TODO()
                Enchantment.KNOCKBACK -> TODO()
                Enchantment.FIRE_ASPECT -> TODO()
                Enchantment.LOOT_BONUS_MOBS -> TODO()
                Enchantment.SWEEPING_EDGE -> TODO()
                Enchantment.DIG_SPEED -> TODO()
                Enchantment.SILK_TOUCH -> TODO()
                Enchantment.DURABILITY -> TODO()
                Enchantment.LOOT_BONUS_BLOCKS -> TODO()
                Enchantment.ARROW_DAMAGE -> TODO()
                Enchantment.ARROW_KNOCKBACK -> TODO()
                Enchantment.ARROW_FIRE -> TODO()
                Enchantment.ARROW_INFINITE -> TODO()
                Enchantment.LUCK -> TODO()
                Enchantment.LURE -> TODO()
                Enchantment.LOYALTY -> TODO()
                Enchantment.IMPALING -> TODO()
                Enchantment.RIPTIDE -> TODO()
                Enchantment.CHANNELING -> TODO()
                Enchantment.MULTISHOT -> TODO()
                Enchantment.QUICK_CHARGE -> TODO()
                Enchantment.PIERCING -> TODO()
                Enchantment.MENDING -> TODO()
                Enchantment.VANISHING_CURSE -> TODO()
                Enchantment.SOUL_SPEED -> TODO()
                Enchantment.SWIFT_SNEAK -> TODO()
                else -> return statusMap
            }
        }

        return statusMap
    }
}