package com.github.zinc.tools

import com.github.zinc.core.player.domain.StatusType
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

        val amplifier = when {
            name.contains(WOOD) -> .0
            name.contains(STONE) -> .5
            name.contains(GOLD) -> .5
            name.contains(IRON) -> 1.0
            name.contains(DIAMOND) -> 1.5
            name.contains(NETHERITE) -> 2.0
            else -> return statusMap
        }

        when {
            name.contains(PICKAXE) -> {
                statusMap[StatusType.BALANCE] = 70
            }
        }

        return statusMap
    }

}