package com.github.zinc.module.item.`object`.trinket

import com.github.zinc.module.item.`object`.Passive
import org.bson.types.ObjectId
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

/**
 * 근접 공격력 10% 증가
 */
class PowerRing(
    override val _id: ObjectId,
    override val name: String,
    override val slot: TrinketSlot
) : Trinket, Passive {
    private val modifier: AttributeModifier
        get() = AttributeModifier("power_ring", 0.1, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

    override fun on(player: Player) {
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.addModifier(modifier)
    }

    override fun off(player: Player) {
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.removeModifier(modifier)
    }
}