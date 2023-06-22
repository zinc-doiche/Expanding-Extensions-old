package com.github.zinc.core.equipment

import com.github.zinc.util.extension.text
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class OceanArmor(armor: ItemStack): ZincEquipment(armor) {
    override fun setStatus() {
        super.setStatus()
        levelConstraint += 50
    }

    override fun getLore(): List<Component> = (super.getLore() as MutableList<Component>).apply {
        add(text(""))
        add(text("바다의 가호"))
    }

    companion object {
        val KEY = NamespacedKey.minecraft("ocean_armor")
    }
}