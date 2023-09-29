package com.github.zinc.module.item.`object`.trinket

import com.github.zinc.module.item.`object`.Passive
import com.github.zinc.util.item
import com.github.zinc.util.list
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bson.types.ObjectId
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 근접 공격력 10% 증가
 */
class PowerRing: Trinket, Passive {
    override val name = "power_ring"
    override val slot = TrinketSlot.RING
    private val modifier: AttributeModifier
        get() = AttributeModifier(name, 0.1, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

    override fun getItem(): ItemStack = trinketItem(
        Material.REDSTONE,
        text("힘의 반지", NamedTextColor.GOLD, TextDecoration.BOLD),
        TrinketSlot.RING,
        text("근접 공격력 10% 증가", NamedTextColor.GRAY),
    )

    override fun on(player: Player) {
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.addModifier(modifier)
    }

    override fun off(player: Player) {
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.removeModifier(modifier)
    }
}